package com.aichuangyi.core.cache;

/**
 * @author Mr.Min
 * @description 缓存工厂接口
 * @date 2025/8/12
 **/
public interface CacheFactory {

    /**
     * 创建缓存实例
     *
     * @param name
     * @param config
     * @param clazz
     * @param <V>
     * @return
     */
    <V> CacheManager<String, V> createCache(String name, CacheConfig config, Class<V> clazz);

    /**
     * 创建缓存实例(使用默认配置)
     *
     * @param name
     * @param clazz
     * @param <V>
     * @return
     */
    default <V> CacheManager<String, V> createCache(String name, Class<V> clazz) {
        return createCache(name, CacheConfig.defaultConfig(), clazz);
    }

    /**
     * 创建缓存实例(使用默认配置)
     *
     * @param clazz
     * @param <V>
     * @return
     */
    default <V> CacheManager<String, V> createCache(Class<V> clazz) {
        return createCache("cache", clazz);
    }
}