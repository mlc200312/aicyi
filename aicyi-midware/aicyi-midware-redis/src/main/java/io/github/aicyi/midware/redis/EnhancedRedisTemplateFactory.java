package io.github.aicyi.midware.redis;

import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.lang.NonNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 企业级增强版 RedisTemplate 工厂
 * <p>
 * 优化点：
 * 1. RedisTemplate 缓存复用
 * 2. Serializer 缓存
 * 3. 消除重复代码
 * 4. ObjectMapper 隔离
 * 5. 支持 Class / Type
 * 6. XML Serializer 缓存
 * 7. 泛型增强
 * 8. 更好的扩展性
 *
 * @author Mr.Min
 */
public class EnhancedRedisTemplateFactory {

    /**
     * Redis连接工厂
     */
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 独立 JsonCodec，避免外部污染
     */
    private final JsonCodec jsonCodec;

    /**
     * RedisTemplate 缓存
     */
    private final Map<String, RedisTemplate<?, ?>> templateCache = new ConcurrentHashMap<>();

    /**
     * value Serializer 缓存
     */
    private final Map<String, RedisSerializer<?>> valueSerializerCache = new ConcurrentHashMap<>();

    /**
     * 通用 Generic Serializer（单例）
     */
    private final JsonCodecRedisSerializer genericJsonSerializer = new JsonCodecRedisSerializer(Object.class);

    /**
     * JDK Serializer（单例）
     */
    private final JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

    /**
     * String Serializer（单例）
     */
    private final RedisSerializer<String> stringSerializer = RedisSerializer.string();

    public EnhancedRedisTemplateFactory(@NonNull RedisConnectionFactory redisConnectionFactory, @NonNull JsonCodec jsonCodec) {

        this.redisConnectionFactory = Objects.requireNonNull(redisConnectionFactory);
        this.jsonCodec = Objects.requireNonNull(jsonCodec);
    }

    public EnhancedRedisTemplateFactory(@NonNull RedisConnectionFactory redisConnectionFactory) {
        this(redisConnectionFactory, JacksonJsonCodec.DEFAULT);
    }

    /**
     * 获取 StringRedisTemplate
     */
    public StringRedisTemplate getStringRedisTemplate() {

        return (StringRedisTemplate) templateCache.computeIfAbsent(
                "string_template",
                key -> {
                    StringRedisTemplate template = new StringRedisTemplate();
                    template.setConnectionFactory(redisConnectionFactory);
                    template.afterPropertiesSet();
                    return template;
                }
        );
    }

    // =========================================================
    // JSON TEMPLATE
    // =========================================================

    /**
     * JSON RedisTemplate（Class）
     */
    public <T> RedisTemplate<String, T> getJsonRedisTemplate(Class<T> clazz) {

        String cacheKey = "json:" + clazz.getName();

        return cast(
                templateCache.computeIfAbsent(
                        cacheKey,
                        key -> {

                            RedisSerializer<?> serializer = getOrCreateValueSerializer(clazz);

                            return createTemplate(serializer);
                        }
                )
        );
    }

    /**
     * JSON RedisTemplate（Type）
     */
    public <T> RedisTemplate<String, T> getJsonRedisTemplate(Type type) {

        RedisSerializer<?> serializer = getOrCreateValueSerializer(type);

        return cast(
                createTemplate(serializer)
        );
    }

    /**
     * 通用 JSON Template
     */
    public RedisTemplate<String, Object> getGenericJsonRedisTemplate() {

        return (RedisTemplate<String, Object>) getRedisTemplate(SerializerType.JSON);
    }

    // =========================================================
    // GENERIC TEMPLATE
    // =========================================================

    /**
     * 获取通用 RedisTemplate
     */
    public RedisTemplate<String, Object> getRedisTemplate(SerializerType serializerType) {

        String cacheKey = "generic:" + serializerType.name();

        return cast(
                templateCache.computeIfAbsent(
                        cacheKey,
                        key -> createTemplate(
                                resolveSerializer(serializerType)
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
    private <T> RedisTemplate<String, T> createTemplate(RedisSerializer<T> valueSerializer) {

        RedisTemplate<String, T> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        // key serializer
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // value serializer
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        // default serializer
        template.setEnableDefaultSerializer(false);

        // 是否开启事务（按需开启）
        // template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * 获取 XML Serializer（Class）
     */
    private RedisSerializer<?> getOrCreateValueSerializer(Type type) {

        JsonCodecRedisSerializer<?> serializer = new JsonCodecRedisSerializer<>(type);

        serializer.setJsonCodec(jsonCodec);

        if (type instanceof Class) {
            return cast(
                    valueSerializerCache.computeIfAbsent(
                            type.getTypeName(),
                            key -> serializer
                    )
            );
        }
        return cast(
                serializer
        );
    }

    /**
     * Serializer 路由
     */
    private RedisSerializer<?> resolveSerializer(SerializerType serializerType) {

        switch (serializerType) {

            case JDK:
                return jdkSerializer;

            case JSON:
                return genericJsonSerializer;

            default:
                return stringSerializer;
        }
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

    public JsonCodec getJsonCodec() {
        return jsonCodec;
    }

    /**
     * 清空缓存（测试场景可用）
     */
    public void clearCache() {
        templateCache.clear();
    }
}