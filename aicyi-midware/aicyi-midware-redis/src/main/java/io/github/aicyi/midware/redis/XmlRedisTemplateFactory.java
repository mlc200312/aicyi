package io.github.aicyi.midware.redis;

import io.github.aicyi.commons.util.Assert;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.OxmSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Min
 * @description Xml RedisTemplate 工厂
 * @date 2026/5/27
 **/
public class XmlRedisTemplateFactory {

    /**
     * Redis连接工厂
     */
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * RedisTemplate 缓存
     */
    private final Map<String, RedisTemplate<?, ?>> templateCache = new ConcurrentHashMap<>();

    /**
     * XML Serializer 缓存
     */
    private final Map<String, RedisSerializer<?>> xmlSerializerCache = new ConcurrentHashMap<>();

    /**
     * String Serializer（单例）
     */
    private final RedisSerializer<String> stringSerializer = RedisSerializer.string();

    public XmlRedisTemplateFactory(@NonNull RedisConnectionFactory redisConnectionFactory) {

        Assert.notNull(redisConnectionFactory, "redisConnectionFactory");
        this.redisConnectionFactory = redisConnectionFactory;
    }

    // =========================================================
    // XML TEMPLATE
    // =========================================================

    /**
     * XML RedisTemplate（Class）
     */
    public <T> RedisTemplate<String, T> getXmlRedisTemplate(Class<T> clazz) {

        String cacheKey = "xml:" + clazz.getName();

        return cast(
                templateCache.computeIfAbsent(
                        cacheKey,
                        key -> createTemplate(getOrCreateXmlSerializer(clazz))
                )
        );
    }

    /**
     * XML RedisTemplate（package scan）
     */
    public <T> RedisTemplate<String, T> getXmlRedisTemplate(String packagesToScan) {

        String cacheKey = "xml:package:" + packagesToScan;

        return cast(
                templateCache.computeIfAbsent(
                        cacheKey,
                        key -> createTemplate(
                                getOrCreateXmlSerializer(packagesToScan)
                        )
                )
        );
    }

    // =========================================================
    // INTERNAL
    // =========================================================

    /**
     * 创建 RedisTemplate
     */
    private RedisTemplate<String, Object> createTemplate(RedisSerializer<?> valueSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        // key serializer
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // value serializer
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        // default serializer
        template.setDefaultSerializer(valueSerializer);

        // 是否开启事务（按需开启）
        // template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * 获取 XML Serializer（Class）
     */
    private RedisSerializer<?> getOrCreateXmlSerializer(Class<?> clazz) {

        return xmlSerializerCache.computeIfAbsent(
                clazz.getName(),
                key -> {

                    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
                    marshaller.setClassesToBeBound(clazz);
                    try {
                        marshaller.afterPropertiesSet();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    return new OxmSerializer(marshaller, marshaller);
                }
        );
    }

    /**
     * 获取 XML Serializer（Package）
     */
    private RedisSerializer<?> getOrCreateXmlSerializer(String packagesToScan) {

        return xmlSerializerCache.computeIfAbsent(
                packagesToScan,
                key -> {

                    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
                    marshaller.setPackagesToScan(packagesToScan);
                    try {
                        marshaller.afterPropertiesSet();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    return new OxmSerializer(marshaller, marshaller);
                }
        );
    }

    /**
     * 泛型转换
     */
    @SuppressWarnings("unchecked")
    private <T> T cast(Object obj) {
        return (T) obj;
    }

    // =========================================================
    // GETTER
    // =========================================================

    public RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    /**
     * 清空缓存（测试场景可用）
     */
    public void clearCache() {
        templateCache.clear();
        xmlSerializerCache.clear();
    }
}