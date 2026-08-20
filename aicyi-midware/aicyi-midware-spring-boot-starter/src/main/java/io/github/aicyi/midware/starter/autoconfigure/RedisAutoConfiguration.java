package io.github.aicyi.midware.starter.autoconfigure;

import io.github.aicyi.midware.redis.template.EnhancedRedisTemplateFactory;
import io.github.aicyi.commons.core.lock.DistributedLockManager;
import io.github.aicyi.midware.redis.lock.RedissonDistributedLockManager;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
 * 需同时覆盖 spring-data-redis 与 aicyi-midware-redis 两侧的类，
 * 业务未引入 redis 时整个配置类跳过加载，避免 NoClassDefFoundError
 * @date 10:34
 **/
@AutoConfiguration
@ConditionalOnClass({RedisConnectionFactory.class, EnhancedRedisTemplateFactory.class})
@ConditionalOnProperty(prefix = "aicyi.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EnhancedRedisTemplateFactory enhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory) {
        return new EnhancedRedisTemplateFactory(redisConnectionFactory);
    }

    /**
     * 容器中存在 RedissonClient Bean 时才装配（类存在不代表 Bean 存在，框架不生产 RedissonClient）
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedissonClient.class)
    public DistributedLockManager distributedLockManager(RedissonClient redissonClient) {
        return new RedissonDistributedLockManager(redissonClient);
    }
}
