package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.cache.CacheConfig;
import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.template.cache.TemplateCacheManager;
import io.github.aicyi.midware.message.template.cache.TemplateLocalCache;
import io.github.aicyi.midware.message.template.cache.TemplateRemoteCache;
import io.github.aicyi.midware.message.template.mapper.MessageTemplateMapper;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import io.github.aicyi.commons.util.serializer.CacheWrapperPrincipalSerializer;
import io.github.aicyi.midware.redis.cache.RedisCache;
import io.github.aicyi.midware.redis.cache.RedisCacheConfig;
import io.github.aicyi.midware.redis.cache.RedissonCacheLock;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 模版自动配置
 * @date 22:57
 **/
@AutoConfiguration
@MapperScan(basePackages = {"io.github.aicyi.midware.message.template.mapper"})
public class TemplateAutoConfiguration {

    private final EnhancedRedisTemplateFactory templateFactory;
    private final RedissonClient redissonClient;

    public TemplateAutoConfiguration(EnhancedRedisTemplateFactory templateFactory, RedissonClient redissonClient) {
        this.templateFactory = templateFactory;
        this.redissonClient = redissonClient;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(EnhancedRedisTemplateFactory.class)
    public TemplateProvider templateProvider(MessageTemplateMapper templateMapper) {
        StringRedisTemplate stringRedisTemplate = templateFactory.getStringRedisTemplate();

        CacheConfig cacheConfig = RedisCacheConfig.builder()
                .globalPrefix("cache")
                .cacheName("message_template")
                .ttl(Duration.ofDays(1))
                .cacheNull(true)
                .serializer(new CacheWrapperPrincipalSerializer<>(MessageTemplate.class))
                .build();

        RedissonCacheLock redissonCacheLock = new RedissonCacheLock(redissonClient);

        RedisCache<MessageTemplate> messageTemplateCache = new RedisCache<>(stringRedisTemplate, cacheConfig, redissonCacheLock);

        // 本地缓存
        TemplateLocalCache templateLocalCache = new TemplateLocalCache();

        // 远程缓存
        TemplateRemoteCache templateRemoteCache = new TemplateRemoteCache(messageTemplateCache);

        return new TemplateCacheManager(templateLocalCache, templateRemoteCache, templateMapper);
    }
}
