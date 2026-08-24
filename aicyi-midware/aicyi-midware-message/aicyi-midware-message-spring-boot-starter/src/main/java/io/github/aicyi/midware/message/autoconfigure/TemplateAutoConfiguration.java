package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.cache.CacheConfig;
import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.template.cache.TemplateCacheManager;
import io.github.aicyi.midware.message.template.cache.TemplateLocalCache;
import io.github.aicyi.midware.message.template.cache.TemplateRemoteCache;
import io.github.aicyi.midware.message.template.mapper.MessageTemplateMapper;
import io.github.aicyi.midware.redis.template.EnhancedRedisTemplateFactory;
import io.github.aicyi.commons.util.codec.CacheWrapperCodec;
import io.github.aicyi.midware.redis.cache.RedisCache;
import io.github.aicyi.midware.redis.cache.RedisCacheConfig;
import io.github.aicyi.midware.redis.cache.RedissonCacheLock;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 模版自动配置
 * <p>
 * 装配条件：aicyi.message.template.enabled=true，且 classpath 同时存在 Redisson 与 MyBatis
 * （SqlSessionFactory），且容器中已存在 EnhancedRedisTemplateFactory 与 RedissonClient 两个契约 Bean。
 * 本模块对 Redis/Redisson/MyBatis 均为常规编译依赖，引入模块即具备类路径；
 * 类级守卫用于显式表达装配契约，并防止运行期被人为排除依赖后触发 NoClassDefFoundError。
 * <p>
 * EnhancedRedisTemplateFactory 由 Redis 自动配置（aicyi.redis.enabled=true）生产，
 * RedissonClient 由业务自行定义；任一缺失时本配置整体静默跳过，不再以构造器注入方式启动失败。
 * @date 22:57
 **/
@AutoConfiguration
@AutoConfigureAfter(name = "io.github.aicyi.midware.starter.autoconfigure.RedisAutoConfiguration")
@ConditionalOnProperty(
        prefix = "aicyi.message.template",
        name = "enabled",
        havingValue = "true")
@ConditionalOnClass({RedissonClient.class, SqlSessionFactory.class})
@ConditionalOnBean({EnhancedRedisTemplateFactory.class, RedissonClient.class})
public class TemplateAutoConfiguration {

    private final EnhancedRedisTemplateFactory templateFactory;
    private final RedissonClient redissonClient;

    public TemplateAutoConfiguration(EnhancedRedisTemplateFactory templateFactory, RedissonClient redissonClient) {
        this.templateFactory = templateFactory;
        this.redissonClient = redissonClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateProvider templateProvider(MessageTemplateMapper templateMapper) {
        StringRedisTemplate stringRedisTemplate = templateFactory.getStringRedisTemplate();

        CacheConfig cacheConfig = RedisCacheConfig.builder()
                .globalPrefix("cache")
                .cacheName("message_template")
                .ttl(Duration.ofDays(1))
                .cacheNull(true)
                .build();

        RedissonCacheLock redissonCacheLock = new RedissonCacheLock(redissonClient);

        RedisCache<MessageTemplate> messageTemplateCache = new RedisCache<>(
                stringRedisTemplate,
                cacheConfig,
                new CacheWrapperCodec<>(MessageTemplate.class),
                redissonCacheLock
        );

        // 本地缓存
        TemplateLocalCache templateLocalCache = new TemplateLocalCache();

        // 远程缓存
        TemplateRemoteCache templateRemoteCache = new TemplateRemoteCache(messageTemplateCache);

        return new TemplateCacheManager(templateLocalCache, templateRemoteCache, templateMapper);
    }

    /**
     * Mapper 扫描独立为嵌套配置：仅当外层条件（模板开关 + Redisson/MyBatis 均存在）满足时才注册扫描器，
     * 保证 Mapper Bean 在 Provider 装配前就绪
     */
    @Configuration
    @MapperScan(basePackages = {"io.github.aicyi.midware.message.template.mapper"})
    static class MessageTemplateMapperScanConfiguration {
    }
}
