package com.aicyiframework.redis;

import com.aichuangyi.commons.util.json.JacksonConverter;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author Mr.Min
 * @description RedisTemplate 工厂类
 * @date 2025/8/11
 **/
public class EnhancedRedisTemplateFactory {

    private RedisConnectionFactory redisConnectionFactory;

    public EnhancedRedisTemplateFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public EnhancedRedisTemplateFactory(String host, int port, String password, boolean useLettuce) {
        this.redisConnectionFactory = createConnectionFactory(host, port, password, useLettuce);
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

        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    public <T> RedisTemplate<String, T> getTemplate(JavaType javaType) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 键始终使用字符串序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(javaType);
        jackson2JsonRedisSerializer.setObjectMapper(new JacksonConverter());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    public <T> RedisTemplate<String, T> getTemplate(SerializerType serializerType, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
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
                JacksonConverter jacksonConverter = new JacksonConverter();
                JavaType javaType = jacksonConverter.constructType(clazz);
                Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(javaType);
                jackson2JsonRedisSerializer.setObjectMapper(jacksonConverter);
                template.setValueSerializer(jackson2JsonRedisSerializer);
                template.setHashValueSerializer(jackson2JsonRedisSerializer);
                break;
            case XML:
                Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
                marshaller.setClassesToBeBound(clazz);

                Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
                unmarshaller.setClassesToBeBound(clazz);

                OxmSerializer oxmSerializer = new OxmSerializer(marshaller, unmarshaller);

                template.setValueSerializer(oxmSerializer);
                template.setHashValueSerializer(oxmSerializer);
                break;
            case STRING:
                template.setValueSerializer(new GenericToStringSerializer<>(clazz));
                template.setHashValueSerializer(new GenericToStringSerializer<>(clazz));
                break;
            default:
                template.setValueSerializer(new StringRedisSerializer());
                template.setHashValueSerializer(new StringRedisSerializer());
        }

        template.afterPropertiesSet();
        return template;
    }

    public enum SerializerType {
        JDK, JSON, XML, STRING
    }

    /**
     * 创建连接工厂
     *
     * @param host
     * @param port
     * @param password
     * @param useLettuce
     * @return
     */
    private RedisConnectionFactory createConnectionFactory(String host, int port, String password, boolean useLettuce) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }
        return useLettuce ? new LettuceConnectionFactory(config) : new JedisConnectionFactory(config);
    }
}