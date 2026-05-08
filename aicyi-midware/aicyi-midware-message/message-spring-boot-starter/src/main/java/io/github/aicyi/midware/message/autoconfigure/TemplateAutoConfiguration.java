package io.github.aicyi.midware.message.autoconfigure;

import com.fasterxml.jackson.databind.JavaType;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;
import io.github.aicyi.midware.message.core.template.MessageTemplate;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.sms.template.TemplateRender;
import io.github.aicyi.midware.message.template.cache.TemplateCacheManager;
import io.github.aicyi.midware.message.template.cache.TemplateLocalCache;
import io.github.aicyi.midware.message.template.cache.TemplateRedisCache;
import io.github.aicyi.midware.message.template.mapper.MessageTemplateMapper;
import io.github.aicyi.midware.message.sms.template.DefaultTemplateRender;
import io.github.aicyi.midware.redis.cache.RedisCacheFactory;
import io.github.aicyi.midware.redis.cache.RedisCacheManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author Mr.Min
 * @description 模版自动配置
 * @date 22:57
 **/
@AutoConfiguration
@MapperScan(basePackages = {"io.github.aicyi.midware.message.template.mapper"})
public class TemplateAutoConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;

    public TemplateAutoConfiguration(@Autowired(required = false) RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateRender templateRender() {
        return new DefaultTemplateRender();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(RedisConnectionFactory.class)
    public TemplateProvider templateProvider(MessageTemplateMapper templateMapper) {
        // 本地缓存
        TemplateLocalCache templateLocalCache = new TemplateLocalCache();
        // redis缓存
        RedisCacheFactory redisCacheFactory = new RedisCacheFactory(redisConnectionFactory);
        JavaType javaType = JacksonTypeFactory.typeOf(MessageTemplate.class);
        RedisCacheManager<MessageTemplate> redisCacheManager = redisCacheFactory.createCache("messageTemplate", javaType);
        TemplateRedisCache templateRedisCache = new TemplateRedisCache(redisCacheManager);
        return new TemplateCacheManager(templateLocalCache, templateRedisCache, templateMapper);
    }
}
