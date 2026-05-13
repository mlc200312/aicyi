package io.github.aicyi.midware.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author Mr.Min
 * @description RedisTemplate 工厂类
 * @date 2025/8/11
 **/
public class EnhancedRedisTemplateFactory {

    private static final StringRedisSerializer STRING_REDIS_SERIALIZER = new StringRedisSerializer();

    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    public EnhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.objectMapper = objectMapper;
    }

    public EnhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory) {
        this(redisConnectionFactory, JacksonJsonCodec.DEFAULT.getObjectMapper());
    }

    public RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public StringRedisTemplate getStringRedisTemplate() {

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);

        stringRedisTemplate.afterPropertiesSet();

        return stringRedisTemplate;
    }

    public <T> RedisTemplate<String, T> getJsonRedisTemplate(JavaType javaType) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(javaType);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

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
    public <T> RedisTemplate<String, T> getXmlRedisTemplate(Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

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
    public <T> RedisTemplate<String, T> getXmlRedisTemplate(String packagesToScan) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

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

    public RedisTemplate<String, Object> getGenericRedisTemplate(SerializerType serializerType) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        // 根据类型设置值序列化器
        switch (serializerType) {
            case JDK:
                template.setValueSerializer(new JdkSerializationRedisSerializer());
                template.setHashValueSerializer(new JdkSerializationRedisSerializer());
                break;
            case JSON:
                GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
                template.setValueSerializer(genericJackson2JsonRedisSerializer);
                template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
                break;
            case STRING:
                template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
                template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
                break;
            default:
                template.setValueSerializer(RedisSerializer.string());
                template.setHashValueSerializer(RedisSerializer.string());
        }

        template.afterPropertiesSet();
        return template;
    }

    public enum SerializerType {
        JDK, JSON, STRING
    }
}