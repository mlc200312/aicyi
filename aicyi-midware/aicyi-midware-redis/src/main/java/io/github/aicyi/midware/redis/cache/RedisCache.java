package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.*;
import io.github.aicyi.commons.util.Assert;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Mr.Min
 * @description Redis缓存实现类
 * @date 2026/5/22
 **/
public class RedisCache<T> implements Cache<String, T> {

    private final StringRedisTemplate template;
    private final CacheConfig<CacheWrapper<T>> config;
    private final CacheLock lock;
    private final CacheStats stats = new CacheStats();

    public RedisCache(StringRedisTemplate template, CacheConfig<CacheWrapper<T>> config, CacheLock lock) {

        Assert.notNull(template, "template");
        Assert.notNull(config, "config");

        this.template = template;
        this.config = config;
        this.lock = lock;
    }

    public RedisCache(StringRedisTemplate template, CacheConfig<CacheWrapper<T>> config) {

        this(template, config, new RedisCacheLock(template));
    }

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

    private String lockKey(String key) {
        return "lock:" + buildKey(key);
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

    private Duration ttlWithJitter(Duration ttl) {
        if (ttl == null || !config.isTtlJitter()) {
            return ttl;
        }

        long seconds = ttl.getSeconds();
        long maxJitter = seconds * config.getJitterPercent() / 100;

        long jitter = ThreadLocalRandom.current().nextLong(maxJitter + 1);

        return Duration.ofSeconds(seconds + jitter);
    }

    private CacheWrapper<T> lookup(String key) {
        String value = template.opsForValue().get(buildKey(key));

        if (value == null) {
            stats.recordMiss();
            return CacheWrapper.miss();
        }

        stats.recordHit();

        return config.getSerializer().deserialize(value);
    }

    private String wrapValue(T value) {

        CacheWrapper<T> cacheWrapper;

        if (null == value && !config.isCacheNull()) {

            cacheWrapper = CacheWrapper.miss();
        } else {

            cacheWrapper = CacheWrapper.hit(value);
        }

        return config.getSerializer().serialize(cacheWrapper);
    }

    private T unwrapValue(String value) {

        CacheWrapper<T> wrapper = config.getSerializer().deserialize(value);

        return wrapper.getData();
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

        if (lock.tryLock(lockKey, config.getLockTtl())) {
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
                lock.unlock(lockKey);
            }
        }

        long waitUntil = System.currentTimeMillis() + config.getWaitTimeout().toMillis();

        while (System.currentTimeMillis() < waitUntil) {

            result = lookup(key);

            if (!result.isNullValue()) {
                return result.getData();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return loader.load(key);
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

        return IntStream.range(0, originalKeys.size())
                .filter(i -> values.get(i) != null)
                .boxed()
                .collect(Collectors.toMap(
                        originalKeys::get,
                        i -> unwrapValue(values.get(i))
                ));
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        return template.getExpire(buildKey(key), timeUnit);
    }

    @Override
    public void put(String key, T value) {
        put(key, value, config.getTtl());
    }

    @Override
    public void put(String key, T value, Duration ttl) {
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
        if (values == null || values.isEmpty()) {
            return;
        }

        Map<String, String> redisMap = values.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> buildKey(e.getKey()),
                        e -> wrapValue(e.getValue())
                ));

        template.opsForValue().multiSet(redisMap);

        if (config.getTtl() != null) {
            Duration ttl = ttlWithJitter(config.getTtl());

            redisMap.keySet().forEach(key -> template.expire(key, ttl));
        }

        stats.recordPut();
    }

    @Override
    public void evict(String key) {
        template.delete(buildKey(key));
        stats.recordEvict();
    }

    @Override
    public void evictAll(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<String> redisKeys = keys.stream().map(this::buildKey).collect(Collectors.toList());

        template.delete(redisKeys);
        stats.recordEvict();
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(template.hasKey(buildKey(key)));
    }

    @Override
    public void clear() {
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern())
                .count(500)
                .build();

        template.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    @Override
    public CacheStats stats() {
        return stats;
    }
}