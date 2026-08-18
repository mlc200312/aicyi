package io.github.aicyi.midware.redis;

import io.github.aicyi.commons.core.codec.JsonCodec;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 企业级增强版 RedisTemplate 工厂
 * <p>
 * 优化点：
 * 1. RedisTemplate 缓存复用（Class 与 Type 均缓存）
 * 2. Serializer 缓存
 * 3. 消除重复代码（公共逻辑下沉至 {@link AbstractRedisTemplateFactory}）
 * 4. ObjectMapper 隔离
 * 5. 支持 Class / Type
 * 6. 泛型增强
 * 7. 更好的扩展性
 *
 * @author Mr.Min
 */
public class EnhancedRedisTemplateFactory extends AbstractRedisTemplateFactory {

    /**
     * 独立 JsonCodec，避免外部污染
     */
    private final JsonCodec jsonCodec;

    /**
     * value Serializer 缓存（按 Type 名称）
     */
    private final Map<String, RedisSerializer<?>> valueSerializerCache = new ConcurrentHashMap<>();

    /**
     * 通用 Generic Serializer（单例，使用工厂注入的 JsonCodec）
     */
    private final JsonCodecRedisSerializer<Object> genericJsonSerializer;

    /**
     * JDK Serializer（单例）
     */
    private final JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

    public EnhancedRedisTemplateFactory(@NonNull RedisConnectionFactory redisConnectionFactory, @NonNull JsonCodec jsonCodec) {

        super(redisConnectionFactory);

        Assert.notNull(jsonCodec, "jsonCodec");
        this.jsonCodec = jsonCodec;

        this.genericJsonSerializer = new JsonCodecRedisSerializer<>(Object.class);
        this.genericJsonSerializer.setJsonCodec(jsonCodec);
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
        return getJsonRedisTemplate((Type) clazz);
    }

    /**
     * JSON RedisTemplate（Type，按类型名称缓存复用）
     */
    public <T> RedisTemplate<String, T> getJsonRedisTemplate(Type type) {

        String cacheKey = "json:" + type.getTypeName();

        return cast(
                templateCache.computeIfAbsent(
                        cacheKey,
                        key -> createTemplate(getOrCreateValueSerializer(type), false)
                )
        );
    }

    /**
     * 通用 JSON Template
     */
    public RedisTemplate<String, Object> getGenericJsonRedisTemplate() {

        return getRedisTemplate(SerializerType.JSON);
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
                        key -> createTemplate(resolveSerializer(serializerType), false)
                )
        );
    }

    // =========================================================
    // INTERNAL
    // =========================================================

    /**
     * 获取 JSON Serializer（按 Type 名称缓存）
     */
    private RedisSerializer<?> getOrCreateValueSerializer(Type type) {

        return cast(
                valueSerializerCache.computeIfAbsent(
                        type.getTypeName(),
                        key -> {
                            JsonCodecRedisSerializer<?> serializer = new JsonCodecRedisSerializer<>(type);
                            serializer.setJsonCodec(jsonCodec);
                            return serializer;
                        }
                )
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

    // =========================================================
    // GETTER
    // =========================================================

    public JsonCodec getJsonCodec() {
        return jsonCodec;
    }

    /**
     * 清空缓存（测试场景可用）
     */
    @Override
    public void clearCache() {
        super.clearCache();
        valueSerializerCache.clear();
    }
}
