package com.aicyiframework.data.redis.cache;

import com.aicyiframework.core.cache.CacheConfig;
import com.aicyiframework.core.cache.CacheFactory;
import com.aichuangyi.commons.util.json.JacksonConverter;
import com.aicyiframework.data.redis.EnhancedRedisTemplateFactory;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Mr.Min
 * @description Redis缓存工厂
 * @date 14:03
 **/
public class RedisCacheFactory implements CacheFactory {

    private final EnhancedRedisTemplateFactory enhancedRedisTemplateFactory;

    public RedisCacheFactory(RedisConnectionFactory redisConnectionFactory, JacksonConverter jacksonConverter) {
        this.enhancedRedisTemplateFactory = new EnhancedRedisTemplateFactory(redisConnectionFactory, jacksonConverter);
    }

    public RedisCacheFactory(RedisConnectionFactory redisConnectionFactory) {
        this.enhancedRedisTemplateFactory = new EnhancedRedisTemplateFactory(redisConnectionFactory);
    }

    @Override
    public RedisCacheManager<Object> createCache(String name, CacheConfig config) {
        RedisTemplate<String, Object> redisTemplate = enhancedRedisTemplateFactory.getGenericTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON);
        return new RedisCacheManager<>(redisTemplate, name);
    }

    public <V> RedisCacheManager<V> createCache(String name, JavaType javaType) {
        RedisTemplate<String, V> redisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(javaType);
        return new RedisCacheManager<>(redisTemplate, name);
    }

    public <V> RedisCacheManager<V> createCache(JavaType javaType) {
        return createCache("cache", javaType);
    }
}
