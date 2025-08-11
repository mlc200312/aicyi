package com.aichuangyi.demo.config;

import com.aicyiframework.redis.EnhancedRedisTemplateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 12:02
 **/
@Configuration
public class RedisConfig {

    @Bean
    public EnhancedRedisTemplateFactory getEnhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory) {
        return new EnhancedRedisTemplateFactory(redisConnectionFactory);
    }
}
