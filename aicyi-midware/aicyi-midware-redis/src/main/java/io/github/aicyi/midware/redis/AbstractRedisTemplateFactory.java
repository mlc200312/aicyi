package io.github.aicyi.midware.redis;

import io.github.aicyi.commons.lang.Assert;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RedisTemplate 工厂公共基类
 * <p>
 * 统一持有连接工厂、模板缓存与模板创建逻辑，供不同序列化策略的工厂复用
 *
 * @author Mr.Min
 */
public abstract class AbstractRedisTemplateFactory {

    /**
     * Redis连接工厂
     */
    protected final RedisConnectionFactory redisConnectionFactory;

    /**
     * RedisTemplate 缓存
     */
    protected final Map<String, RedisTemplate<?, ?>> templateCache = new ConcurrentHashMap<>();

    /**
     * String Serializer（单例）
     */
    protected final RedisSerializer<String> stringSerializer = RedisSerializer.string();

    protected AbstractRedisTemplateFactory(@NonNull RedisConnectionFactory redisConnectionFactory) {

        Assert.notNull(redisConnectionFactory, "redisConnectionFactory");
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * 创建 RedisTemplate
     *
     * @param valueSerializer       value/hashValue 序列化器
     * @param enableDefaultSerializer 是否保留默认序列化器兜底
     */
    protected RedisTemplate<String, Object> createTemplate(RedisSerializer<?> valueSerializer, boolean enableDefaultSerializer) {

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
        template.setEnableDefaultSerializer(enableDefaultSerializer);

        // 是否开启事务（按需开启）
        // template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * 泛型转换
     */
    @SuppressWarnings("unchecked")
    protected <T> T cast(Object obj) {
        return (T) obj;
    }

    public RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    /**
     * 清空缓存（测试场景可用）
     */
    public void clearCache() {
        templateCache.clear();
    }
}
