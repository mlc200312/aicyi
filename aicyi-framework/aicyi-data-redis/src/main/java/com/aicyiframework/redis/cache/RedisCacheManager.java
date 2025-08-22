package com.aicyiframework.redis.cache;

import com.aicyiframework.core.cache.CacheConfig;
import com.aicyiframework.core.cache.StringCacheManager;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Mr.Min
 * @description Redis缓存
 * @date 12:08
 **/
public class RedisCacheManager<T> implements StringCacheManager<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final CacheConfig cacheConfig;
    private final String cacheName;

    public RedisCacheManager(RedisTemplate<String, T> redisTemplate, CacheConfig cacheConfig, String cacheName) {
        this.redisTemplate = redisTemplate;
        this.cacheConfig = cacheConfig;
        this.cacheName = cacheName;
    }

    public RedisCacheManager(RedisTemplate<String, T> redisTemplate, String cacheName) {
        this(redisTemplate, CacheConfig.defaultConfig(), cacheName);
    }

    public RedisTemplate<String, T> getRedisTemplate() {
        return redisTemplate;
    }

    public String getCacheName() {
        return cacheName;
    }

    @Override
    public void put(String key, T value) {
        put(key, value, cacheConfig.getDefaultExpireTime(), cacheConfig.getDefaultTimeUnit());
    }

    @Override
    public void put(String key, T value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(buildKey(key), value, timeout, unit);
    }

    @Override
    public void putAll(Map<String, T> map) {
        redisTemplate.executePipelined((RedisCallback<T>) connection -> {

            // 批量设置键值对
            connection.stringCommands().mSet(map.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> buildKey(e.getKey()).getBytes(StandardCharsets.UTF_8),
                            e -> ((RedisSerializer<T>) redisTemplate.getValueSerializer()).serialize(e.getValue())
                    )));

            // 为每个键设置相同的过期时间
            long expireSeconds = cacheConfig.getDefaultExpireTime();
            for (String key : map.keySet()) {
                connection.keyCommands().expire(buildKey(key).getBytes(StandardCharsets.UTF_8), expireSeconds);
            }

            return null;
        });
    }

    @Override
    public T get(String key) {
        return redisTemplate.opsForValue().get(buildKey(key));
    }

    @Override
    public Map<String, T> getAll(Collection<String> keys) {
        List<String> keyList = keys.stream().map(item -> buildKey(item)).collect(Collectors.toList());
        List<T> values = redisTemplate.opsForValue().multiGet(keyList);
        return IntStream
                .range(0, keys.size())
                .filter(i -> Objects.nonNull(values.get(i)))
                .boxed()
                .collect(Collectors.toMap(keyList::get, values::get));
    }

    @Override
    public T get(String key, CacheLoader<String, T> loader) {
        return Optional.ofNullable(get(key)).orElse(loader.load(key));
    }

    @Override
    public T get(String key, CacheLoader<String, T> loader, long timeout, TimeUnit unit) {
        //获取value
        T value = get(key, loader);

        //缓存value
        put(buildKey(key), value, timeout, unit);
        return value;
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(buildKey(key));
    }

    @Override
    public void removeAll(Collection<String> keys) {
        List<String> keyList = keys.stream().map(item -> buildKey(item)).collect(Collectors.toList());
        redisTemplate.delete(keyList);
    }

    @Override
    public boolean containsKey(String key) {
        return redisTemplate.hasKey(buildKey(key));
    }

    @Override
    public Set<String> keys() {
        return redisTemplate.keys(buildKey("*"));
    }

    @Override
    public void clear() {
        redisTemplate.delete(buildKey("*"));
    }

    @Override
    public long size() {
        return keys().size();
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(buildKey(key), timeout, unit);
    }

    @Override
    public long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(buildKey(key), unit);
    }

    @Override
    public T getAndRemove(String key) {
        return redisTemplate.opsForValue().getAndDelete(buildKey(key));
    }

    @Override
    public T getAndReplace(String key, T newValue) {
        return redisTemplate.opsForValue().getAndSet(buildKey(key), newValue);
    }

    private String buildKey(String key) {
        // 添加缓存名前缀
        return cacheName + ":" + key;
    }
}
