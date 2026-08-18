package io.github.aicyi.midware.redis;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

/**
 * EnhancedRedisTemplateFactory 单元测试
 * <p>
 * 重点回归：Class 与 Type 两种入参的模板均缓存复用
 */
class EnhancedRedisTemplateFactoryTest {

    private final RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

    @Test
    void classAndTypeTemplatesShareCache() {

        EnhancedRedisTemplateFactory factory = new EnhancedRedisTemplateFactory(connectionFactory);

        RedisTemplate<String, String> byClass = factory.getJsonRedisTemplate(String.class);
        RedisTemplate<String, String> byType = factory.getJsonRedisTemplate((Type) String.class);

        assertSame(byClass, byType);
    }

    @Test
    void differentTypesGetDifferentTemplates() {

        EnhancedRedisTemplateFactory factory = new EnhancedRedisTemplateFactory(connectionFactory);

        RedisTemplate<String, String> stringTemplate = factory.getJsonRedisTemplate(String.class);
        RedisTemplate<String, Map> mapTemplate = factory.getJsonRedisTemplate((Type) Map.class);

        assertNotSame(stringTemplate, mapTemplate);
    }

    @Test
    void stringTemplateIsCached() {

        EnhancedRedisTemplateFactory factory = new EnhancedRedisTemplateFactory(connectionFactory);

        assertSame(factory.getStringRedisTemplate(), factory.getStringRedisTemplate());
    }
}
