package io.github.aicyi.midware.redis.template;

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
public class XmlRedisTemplateFactory extends AbstractRedisTemplateFactory {

    /**
     * XML Serializer 缓存
     */
    private final Map<String, RedisSerializer<?>> xmlSerializerCache = new ConcurrentHashMap<>();

    public XmlRedisTemplateFactory(@NonNull RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory);
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
                        key -> createTemplate(getOrCreateXmlSerializer(clazz), true)
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
                        key -> createTemplate(getOrCreateXmlSerializer(packagesToScan), true)
                )
        );
    }

    // =========================================================
    // INTERNAL
    // =========================================================

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
                        throw new IllegalStateException("Failed to init Jaxb2Marshaller for class: " + clazz.getName(), e);
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
                        throw new IllegalStateException("Failed to init Jaxb2Marshaller for package: " + packagesToScan, e);
                    }

                    return new OxmSerializer(marshaller, marshaller);
                }
        );
    }

    /**
     * 清空缓存（测试场景可用）
     */
    @Override
    public void clearCache() {
        super.clearCache();
        xmlSerializerCache.clear();
    }
}
