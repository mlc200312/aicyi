package io.github.aicyi.example.config;

import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
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

    @Bean
    public RedissonClient getRedissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase())
                .setConnectionPoolSize(64) // 连接池大小
                .setConnectionMinimumIdleSize(10) // 最小空闲连接数
                .setIdleConnectionTimeout(10000) // 空闲连接超时时间(毫秒)
                .setConnectTimeout(10000) // 连接超时时间(毫秒)
                .setTimeout(3000); // 命令等待超时时间(毫秒)
        return Redisson.create(config);
    }
}
