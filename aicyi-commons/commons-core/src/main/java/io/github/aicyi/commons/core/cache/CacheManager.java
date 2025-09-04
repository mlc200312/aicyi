package io.github.aicyi.commons.core.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 通用缓存接口
 * @date 2025/8/12
 **/
public interface CacheManager<K, V> {

    /**
     * 存入缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    void put(K key, V value);

    /**
     * 存入缓存并设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    void put(K key, V value, long timeout, TimeUnit unit);

    /**
     * 批量存入缓存
     *
     * @param map 键值对集合
     */
    void putAll(Map<K, V> map);

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，不存在返回null
     */
    V get(K key);

    /**
     * 批量获取缓存值
     *
     * @param keys 缓存键集合
     * @return 键值对映射
     */
    Map<K, V> getAll(Collection<K> keys);

    /**
     * 获取缓存值，不存在时通过loader加载
     *
     * @param key    缓存键
     * @param loader 值加载器
     * @return 缓存值
     */
    V get(K key, CacheLoader<K, V> loader);

    /**
     * 获取缓存值，不存在时通过loader加载并设置过期时间
     *
     * @param key     缓存键
     * @param loader  值加载器
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 缓存值
     */
    V get(K key, CacheLoader<K, V> loader, long timeout, TimeUnit unit);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void remove(K key);

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     */
    void removeAll(Collection<K> keys);

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    boolean containsKey(K key);

    /**
     * 获取所有缓存键
     *
     * @return 键集合
     */
    Set<K> keys();

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 获取缓存数量
     *
     * @return 缓存项数量
     */
    long size();

    /**
     * 设置过期时间
     *
     * @param key     缓存键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    boolean expire(K key, long timeout, TimeUnit unit);

    /**
     * 获取剩余存活时间
     *
     * @param key  缓存键
     * @param unit 时间单位
     * @return 剩余时间，单位由参数指定
     */
    long getExpire(K key, TimeUnit unit);

    /**
     * 原子性获取并删除
     *
     * @param key 缓存键
     * @return 被删除的值
     */
    V getAndRemove(K key);

    /**
     * 原子性获取并替换
     *
     * @param key      缓存键
     * @param newValue 新值
     * @return 旧值
     */
    V getAndReplace(K key, V newValue);

    /**
     * 值加载器接口
     */
    @FunctionalInterface
    interface CacheLoader<K, V> {
        V load(K key);
    }
}