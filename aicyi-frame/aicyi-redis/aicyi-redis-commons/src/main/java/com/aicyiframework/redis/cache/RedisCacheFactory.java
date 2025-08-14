package com.aicyiframework.redis.cache;

import com.aichuangyi.core.cache.CacheConfig;
import com.aichuangyi.core.cache.CacheFactory;
import com.aicyiframework.redis.EnhancedRedisTemplateFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Mr.Min
 * @description Redis缓存工厂
 * @date 14:03
 **/
public class RedisCacheFactory implements CacheFactory {

    private final EnhancedRedisTemplateFactory enhancedRedisTemplateFactory;

    public RedisCacheFactory(RedisConnectionFactory redisConnectionFactory) {
        this.enhancedRedisTemplateFactory = new EnhancedRedisTemplateFactory(redisConnectionFactory);
    }

    @Override
    public <V> RedisCacheManager<V> createCache(String name, CacheConfig config, Class<V> clazz) {
        RedisTemplate<String, V> redisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON, clazz);
        return new RedisCacheManager<>(redisTemplate, name);
    }
}
