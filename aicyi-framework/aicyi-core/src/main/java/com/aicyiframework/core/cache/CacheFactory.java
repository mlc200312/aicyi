package com.aicyiframework.core.cache;

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
     * @return
     */
    CacheManager<String, Object> createCache(String name, CacheConfig config);

    /**
     * 创建缓存实例(使用默认配置)
     *
     * @param name
     * @return
     */
    default CacheManager<String, Object> createCache(String name) {
        return createCache(name, CacheConfig.defaultConfig());
    }

    /**
     * 创建缓存实例(使用默认配置)
     *
     * @return
     */
    default CacheManager<String, Object> createCache() {
        return createCache("cache");
    }
}