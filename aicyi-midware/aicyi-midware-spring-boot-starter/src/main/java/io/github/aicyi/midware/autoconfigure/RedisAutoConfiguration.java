package io.github.aicyi.midware.autoconfigure;

import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import io.github.aicyi.commons.core.lock.DistributedLockManager;
import io.github.aicyi.midware.redis.lock.RedissonDistributedLockManager;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author Mr.Min
 * @description Redis 自动配置类
 * <p>
 * 类级 @ConditionalOnClass 守卫：starter 中 redis 模块为 provided+optional，
 * 业务未引入 redis 时整个配置类跳过加载，避免 NoClassDefFoundError
 * @date 10:34
 **/
@AutoConfiguration
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = "aicyi.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EnhancedRedisTemplateFactory getEnhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory) {
        return new EnhancedRedisTemplateFactory(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(RedissonClient.class)
    public DistributedLockManager getDistributedLockManager(RedissonClient redissonClient) {
        return new RedissonDistributedLockManager(redissonClient);
    }
}
