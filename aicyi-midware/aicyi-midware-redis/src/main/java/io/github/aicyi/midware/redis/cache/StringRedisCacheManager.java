package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Mr.Min
 * @description Redis缓存
 * @date 12:08
 **/
public class StringRedisCacheManager extends RedisCacheManager<String> {

    public StringRedisCacheManager(RedisTemplate<String, String> redisTemplate, String cacheName) {
        super(redisTemplate, cacheName);
    }

    public StringRedisCacheManager(RedisConnectionFactory redisConnectionFactory, String cacheName) {
        super(new EnhancedRedisTemplateFactory(redisConnectionFactory).getStringTemplate(), cacheName);
    }
}
