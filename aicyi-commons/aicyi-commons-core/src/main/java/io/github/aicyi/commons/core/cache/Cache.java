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

    /**
     * 缓存未命中时通过 loader 加载并回填缓存（含防击穿锁保护）
     */
    V get(K key, CacheLoader<K, V> loader);

    Map<K, V> getAll(Collection<K> keys);

    /**
     * 批量读取，缺失 key 通过 loader 批量加载并回填缓存。
     * 注意：批量回填不走防击穿锁，高频缺失场景请用单 key {@link #get(Object, CacheLoader)}
     */
    Map<K, V> getAll(Collection<K> keys, CacheLoader<K, V> loader);

    /**
     * 剩余过期时间（推荐入口）
     *
     * @return null 表示 key 不存在；负值表示永久有效；其余为剩余时间
     */
    default Duration getExpire(K key) {
        Long millis = getExpire(key, TimeUnit.MILLISECONDS);
        return millis == null ? null : Duration.ofMillis(millis);
    }

    /**
     * 剩余过期时间
     *
     * @return null 表示 key 不存在；-1 表示永久有效；其余为按 timeUnit 折算的剩余时间
     */
    Long getExpire(K key, TimeUnit timeUnit);

    void put(K key, V value);

    /**
     * ttl 为 null 时表示永久有效（覆盖配置的默认 TTL）
     */
    void put(K key, V value, Duration ttl);

    void putAll(Map<K, V> values);

    /**
     * 批量写入，ttl 为 null 时表示永久有效（覆盖配置的默认 TTL）
     */
    void putAll(Map<K, V> values, Duration ttl);

    /**
     * @return 是否实际删除了 key
     */
    boolean evict(K key);

    /**
     * 批量删除
     *
     * @return 实际删除的 key 数量
     */
    long evictBatch(Collection<K> keys);

    /**
     * key 是否存在
     * <p>
     * 注意：启用缓存空值时，空值占位 key 也返回 true（exists 不代表有业务值）
     */
    boolean exists(K key);

    /**
     * 清空本缓存全部 key
     * <p>
     * 实现必须按 globalPrefix:cacheName 前缀限定范围，不得影响其他缓存
     */
    void clear();

    /**
     * @return 统计快照，外部修改不影响缓存内部计数器
     */
    CacheStats stats();
}