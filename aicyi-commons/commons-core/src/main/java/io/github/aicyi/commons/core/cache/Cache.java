package io.github.aicyi.commons.core.cache;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 缓存接口
 * @date 2026/5/22
 **/
public interface Cache<K, V> {

    V get(K key);

    V get(K key, CacheLoader<K, V> loader);

    Map<K, V> getAll(Collection<K> keys);

    Long getExpire(K key, TimeUnit timeUnit);

    void put(K key, V value);

    void put(K key, V value, Duration ttl);

    void putAll(Map<K, V> values);

    void evict(K key);

    void evictAll(Collection<K> keys);

    boolean exists(K key);

    void clear();

    CacheStats stats();
}