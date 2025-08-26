package com.aicyiframework.data.redis;

import com.aichuangyi.commons.util.json.JacksonConverter;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author Mr.Min
 * @description RedisTemplate 工厂类
 * @date 2025/8/11
 **/
public class EnhancedRedisTemplateFactory {

    private final RedisConnectionFactory redisConnectionFactory;
    private final JacksonConverter jacksonConverter;

    public EnhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory, JacksonConverter jacksonConverter) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.jacksonConverter = jacksonConverter;
    }

    public EnhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory) {
        this(redisConnectionFactory, JacksonConverter.DEFAULT_SIMPLE_CONVERTER);
    }

    public RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    public RedisTemplate<String, String> getStringTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 值始终使用字符串序列化
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    public <T> RedisTemplate<String, T> getJsonTemplate(JavaType javaType) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(javaType);
        jackson2JsonRedisSerializer.setObjectMapper(jacksonConverter);

        // 值使用Jackson序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * * XML模版
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> RedisTemplate<String, T> getXmlTemplate(Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(clazz);

        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(clazz);

        OxmSerializer oxmSerializer = new OxmSerializer(marshaller, unmarshaller);

        template.setValueSerializer(oxmSerializer);
        template.setHashValueSerializer(oxmSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * XML模版
     *
     * @param packagesToScan
     * @param <T>
     * @return
     */
    public <T> RedisTemplate<String, T> getXmlTemplate(String packagesToScan) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(packagesToScan);

        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setPackagesToScan(packagesToScan);

        OxmSerializer oxmSerializer = new OxmSerializer(marshaller, unmarshaller);

        template.setValueSerializer(oxmSerializer);
        template.setHashValueSerializer(oxmSerializer);

        template.afterPropertiesSet();
        return template;
    }

    public RedisTemplate<String, Object> getGenericTemplate(SerializerType serializerType) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 根据类型设置值序列化器
        switch (serializerType) {
            case JDK:
                template.setValueSerializer(new JdkSerializationRedisSerializer());
                template.setHashValueSerializer(new JdkSerializationRedisSerializer());
                break;
            case JSON:
                GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(jacksonConverter);
                template.setValueSerializer(genericJackson2JsonRedisSerializer);
                template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
                break;
            case STRING:
                template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
                template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
                break;
            default:
                template.setValueSerializer(new StringRedisSerializer());
                template.setHashValueSerializer(new StringRedisSerializer());
        }

        template.afterPropertiesSet();
        return template;
    }

    public enum SerializerType {
        JDK, JSON, STRING
    }
}