package io.github.aicyi.midware.redis.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aicyi.commons.core.cache.CacheConfig;
import io.github.aicyi.commons.core.cache.CacheFactory;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
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

    public RedisCacheFactory(EnhancedRedisTemplateFactory factory) {
        this.enhancedRedisTemplateFactory = factory;
    }

    public RedisCacheFactory(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        this(new EnhancedRedisTemplateFactory(redisConnectionFactory, objectMapper));
    }

    public RedisCacheFactory(RedisConnectionFactory redisConnectionFactory) {
        this(new EnhancedRedisTemplateFactory(redisConnectionFactory));
    }

    public EnhancedRedisTemplateFactory getEnhancedRedisTemplateFactory() {
        return enhancedRedisTemplateFactory;
    }

    @Override
    public RedisCacheManager<Object> createCache(String name, CacheConfig config) {
        RedisTemplate<String, Object> redisTemplate = getEnhancedRedisTemplateFactory().getGenericRedisTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON);
        return new RedisCacheManager<>(redisTemplate, name);
    }

    public <V> RedisCacheManager<V> createCache(String name, JavaType javaType) {
        RedisTemplate<String, V> redisTemplate = getEnhancedRedisTemplateFactory().getJsonRedisTemplate(javaType);
        return new RedisCacheManager<>(redisTemplate, name);
    }

    public <V> RedisCacheManager<V> createCache(JavaType javaType) {
        return createCache("cache", javaType);
    }
}
