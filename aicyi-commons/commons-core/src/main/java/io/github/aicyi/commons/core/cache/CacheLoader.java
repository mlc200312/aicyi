package io.github.aicyi.commons.core.cache;

/**
 * @author Mr.Min
 * @description 缓存加载器接口
 * @date 2026/5/22
 **/
@FunctionalInterface
public interface CacheLoader<K, V> {

    V load(K key);
}