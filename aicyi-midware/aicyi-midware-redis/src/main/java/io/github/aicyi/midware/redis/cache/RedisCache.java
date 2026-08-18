package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.*;
import io.github.aicyi.commons.core.codec.StringCodec;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.lang.Assert;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.Min
 * @description Redis缓存实现类
 * @date 2026/5/22
 **/
public class RedisCache<T> implements Cache<String, T> {

    /**
     * 批量写入脚本：SET 与 PX 过期原子完成，避免 multiSet 后逐个 expire 中途失败残留永久 key；
     * 毫秒（PX）口径，保证亚秒级 TTL 不被截断
     */
    private static final DefaultRedisScript<Long> MSET_EX_SCRIPT;

    static {
        MSET_EX_SCRIPT = new DefaultRedisScript<>();
        MSET_EX_SCRIPT.setResultType(Long.class);
        MSET_EX_SCRIPT.setScriptText(
                "local px = tonumber(ARGV[#ARGV]) " +
                        "for i = 1, #KEYS do " +
                        "   redis.call('SET', KEYS[i], ARGV[i], 'PX', px) " +
                        "end " +
                        "return #KEYS"
        );
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final StringRedisTemplate template;
    private final CacheConfig config;
    private final StringCodec<CacheWrapper<T>> serializer;
    private final CacheLock lock;
    private final CacheStats stats = new CacheStats();

    public RedisCache(StringRedisTemplate template,
                      CacheConfig config,
                      StringCodec<CacheWrapper<T>> serializer,
                      CacheLock lock) {

        Assert.notNull(template, "template");
        Assert.notNull(config, "config");
        Assert.notNull(serializer, "serializer");
        Assert.notNull(lock, "lock");

        this.template = template;
        this.config = config;
        this.serializer = serializer;
        this.lock = lock;
    }

    public RedisCache(StringRedisTemplate template,
                      CacheConfig config,
                      StringCodec<CacheWrapper<T>> serializer) {

        this(template, config, serializer, new RedisCacheLock(template));
    }

    /**
     * 暴露底层模板（token 体系等需直接操作 ZSet 等原生结构的内部协作场景使用，
     * 业务代码请勿绕过缓存语义直接操作）
     */
    public StringRedisTemplate getTemplate() {
        return template;
    }

    private String buildKey(String key) {

        Assert.notNull(key, "cache key");

        return Stream.of(
                        config.getGlobalPrefix(),
                        config.getCacheName(),
                        key
                )
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(":"));
    }

    /**
     * 锁 key 置于缓存命名空间内（prefix:cacheName:lock:key），clear() 的前缀扫描可覆盖
     */
    private String lockKey(String key) {
        return buildKey("lock:" + key);
    }

    private String pattern() {
        return Stream.of(
                        config.getGlobalPrefix(),
                        config.getCacheName(),
                        "*"
                )
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(":"));
    }

    /**
     * 正向抖动：TTL 延长 0 ~ jitterPercent%，防止大量 key 同时过期引发雪崩；
     * 毫秒口径，亚秒级 TTL 不会被截断为 0（SET EX 0 为 Redis 非法参数）
     */
    private Duration ttlWithJitter(Duration ttl) {
        if (ttl == null || !config.isTtlJitter()) {
            return ttl;
        }

        long millis = ttl.toMillis();
        long maxJitter = millis * config.getJitterPercent() / 100;

        long jitter = ThreadLocalRandom.current().nextLong(maxJitter + 1);

        return Duration.ofMillis(millis + jitter);
    }

    private CacheWrapper<T> lookup(String key) {
        String value = template.opsForValue().get(buildKey(key));

        if (value == null) {
            stats.recordMiss();
            return CacheWrapper.miss();
        }

        stats.recordHit();

        return serializer.deserialize(value);
    }

    private String wrapValue(T value) {
        return serializer.serialize(CacheWrapper.hit(value));
    }

    @Override
    public T get(String key) {
        return lookup(key).getData();
    }

    @Override
    public T get(String key, CacheLoader<String, T> loader) {
        CacheWrapper<T> result = lookup(key);

        if (!result.isNullValue()) {
            return result.getData();
        }

        String lockKey = lockKey(key);

        CacheLockHandle lockHandle = lock.tryLock(lockKey, config.getLockTtl());

        if (lockHandle != null) {
            long start = System.nanoTime();

            try {
                result = lookup(key);

                if (!result.isNullValue()) {
                    return result.getData();
                }

                T loaded = loader.load(key);

                put(key, loaded);

                stats.recordLoadSuccess(System.nanoTime() - start);

                return loaded;
            } catch (Exception e) {
                stats.recordLoadFailure(System.nanoTime() - start);
                throw e;
            } finally {
                unlockQuietly(lockHandle);
            }
        }

        long waitUntil = System.currentTimeMillis() + config.getWaitTimeout().toMillis();

        boolean interrupted = false;

        while (System.currentTimeMillis() < waitUntil) {

            result = lookup(key);

            if (!result.isNullValue()) {
                return result.getData();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                interrupted = true;
                break;
            }
        }

        if (interrupted) {
            // 被中断后不再执行阻塞加载，直接以最后一次缓存读取结果返回（未命中为 null）
            return result.getData();
        }

        // 等待超时：再竞争一次锁，仅竞争成功者加载，防止多个等待者并发击穿数据源
        CacheLockHandle fallbackHandle = lock.tryLock(lockKey, config.getLockTtl());

        if (fallbackHandle != null) {
            long start = System.nanoTime();

            try {
                result = lookup(key);

                if (!result.isNullValue()) {
                    return result.getData();
                }

                T loaded = loader.load(key);

                put(key, loaded);

                stats.recordLoadSuccess(System.nanoTime() - start);

                return loaded;
            } catch (Exception e) {
                stats.recordLoadFailure(System.nanoTime() - start);
                throw e;
            } finally {
                unlockQuietly(fallbackHandle);
            }
        }

        // 竞争失败：持锁者正在加载，再读一次缓存后穿透返回 null，避免重复击穿
        return lookup(key).getData();
    }

    /**
     * 防御性释放锁：锁过期后的 unlock 失败（如 Redisson 租约过期）不得在 finally 中
     * 掩盖业务返回值或原始异常
     */
    private void unlockQuietly(CacheLockHandle handle) {
        try {
            handle.unlock();
        } catch (Exception e) {
            logger.warn(e, "Cache lock release failed, possibly expired: {}", e.getMessage());
        }
    }

    @Override
    public Map<String, T> getAll(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> originalKeys = new ArrayList<>(keys);

        List<String> redisKeys = originalKeys.stream().map(this::buildKey).collect(Collectors.toList());

        List<String> values = template.opsForValue().multiGet(redisKeys);

        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }

        // 手动收集：Collectors.toMap 对 null value 抛 NPE（空值占位 data 为 null）；
        // 重复 key 与 miss 也在此自然处理
        Map<String, T> result = new LinkedHashMap<>(originalKeys.size());

        for (int i = 0; i < originalKeys.size(); i++) {
            String raw = values.get(i);

            if (raw == null) {
                stats.recordMiss();
                continue;
            }

            stats.recordHit();
            result.put(originalKeys.get(i), serializer.deserialize(raw).getData());
        }

        return result;
    }

    /**
     * 批量读取并回填缺失 key：multiGet 命中部分直返，缺失部分走 loader.loadAll 批量加载后 putAll 回填。
     * 不走防击穿锁，高频缺失场景请用单 key get(key, loader)
     */
    @Override
    public Map<String, T> getAll(Collection<String> keys, CacheLoader<String, T> loader) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, T> result = new LinkedHashMap<>(getAll(keys));

        Set<String> missing = new LinkedHashSet<>(keys);
        missing.removeAll(result.keySet());

        if (missing.isEmpty()) {
            return result;
        }

        long start = System.nanoTime();

        try {
            Map<String, T> loaded = loader.loadAll(missing);

            if (loaded != null && !loaded.isEmpty()) {
                putAll(loaded);
                result.putAll(loaded);
            }

            stats.recordLoadSuccess(System.nanoTime() - start);

            return result;
        } catch (Exception e) {
            stats.recordLoadFailure(System.nanoTime() - start);
            throw e;
        }
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        Long expire = template.getExpire(buildKey(key), timeUnit);

        // 归一 Redis 语义：-2（key 不存在）→ null，-1（永久）保持原样
        return expire == null || expire == -2 ? null : expire;
    }

    @Override
    public void put(String key, T value) {
        put(key, value, config.getTtl());
    }

    /**
     * 写入缓存，ttl 为 null 时表示永久有效（覆盖配置的默认 TTL）。
     * value 为 null 且未开启缓存空值时直接跳过写入，避免占位 key 污染 Redis 与命中率统计
     */
    @Override
    public void put(String key, T value, Duration ttl) {
        if (value == null && !config.isCacheNull()) {
            return;
        }

        String wrapValue = wrapValue(value);

        if (ttl == null) {
            template.opsForValue().set(buildKey(key), wrapValue);
        } else {
            template.opsForValue().set(buildKey(key), wrapValue, ttlWithJitter(ttl));
        }

        stats.recordPut();
    }

    @Override
    public void putAll(Map<String, T> values) {
        putAll(values, config.getTtl());
    }

    /**
     * 批量写入。注意：脚本多 KEYS 要求同 slot（见 package-info 部署限制说明）
     */
    @Override
    public void putAll(Map<String, T> values, Duration ttl) {
        if (values == null || values.isEmpty()) {
            return;
        }

        Map<String, String> redisMap = new LinkedHashMap<>(values.size());

        for (Map.Entry<String, T> entry : values.entrySet()) {
            if (entry.getValue() == null && !config.isCacheNull()) {
                continue;
            }
            redisMap.put(buildKey(entry.getKey()), wrapValue(entry.getValue()));
        }

        if (redisMap.isEmpty()) {
            return;
        }

        if (ttl != null) {
            // 脚本内 SET + PX 原子完成，单次往返，毫秒口径支持亚秒 TTL；TTL 加抖动防雪崩
            template.execute(
                    MSET_EX_SCRIPT,
                    new ArrayList<>(redisMap.keySet()),
                    buildMsetArgs(redisMap.values(), ttlWithJitter(ttl).toMillis())
            );
        } else {
            template.opsForValue().multiSet(redisMap);
        }

        stats.recordPut();
    }

    private Object[] buildMsetArgs(Collection<String> values, long ttlMillis) {
        Object[] args = new Object[values.size() + 1];

        int i = 0;
        for (String value : values) {
            args[i++] = value;
        }
        args[i] = String.valueOf(ttlMillis);

        return args;
    }

    @Override
    public boolean evict(String key) {
        Boolean removed = template.delete(buildKey(key));
        stats.recordEvict();
        return Boolean.TRUE.equals(removed);
    }

    /**
     * 批量删除
     */
    @Override
    public long evictBatch(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0;
        }

        List<String> redisKeys = keys.stream().map(this::buildKey).collect(Collectors.toList());

        Long count = template.delete(redisKeys);
        stats.recordEvict();
        return count == null ? 0 : count;
    }

    @Override
    public boolean exists(String key) {
        // hasKey 为 @Nullable Boolean（事务/管道模式下可能为 null），防御拆箱 NPE
        return Boolean.TRUE.equals(template.hasKey(buildKey(key)));
    }

    /**
     * SCAN 分批删除，避免阻塞；注意：批量 DEL 要求 key 同 slot（见 package-info 部署限制说明）
     */
    @Override
    public void clear() {
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern())
                .count(500)
                .build();

        template.execute((RedisConnection connection) -> {
            // Cursor 在连接关闭时自动释放，此处不显式 close，避免裸 try-with-resources 吞掉 DataAccessException
            Cursor<byte[]> cursor = connection.scan(options);

            List<byte[]> batch = new ArrayList<>();

            while (cursor.hasNext()) {
                batch.add(cursor.next());

                if (batch.size() >= 500) {
                    connection.del(batch.toArray(new byte[0][]));
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                connection.del(batch.toArray(new byte[0][]));
            }
            return null;
        });
    }

    @Override
    public CacheStats stats() {
        return stats.snapshot();
    }
}