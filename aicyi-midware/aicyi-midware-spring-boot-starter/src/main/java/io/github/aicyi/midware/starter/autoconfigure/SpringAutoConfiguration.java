package io.github.aicyi.midware.starter.autoconfigure;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.util.json.jackson.JacksonJsonCodec;
import io.github.aicyi.midware.starter.util.IdUtils;
import io.github.aicyi.midware.starter.util.SpringEnvironmentHelper;
import io.github.aicyi.commons.core.codec.JsonCodec;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Mr.Min
 * @description Spring自动配置类
 * <p>
 * IdGenerator 由 {@link SnowflakeAutoConfiguration} 提供，必须以 @AutoConfigureAfter 显式声明顺序，
 * 避免依赖自动配置类的隐式排序导致 {@code @ConditionalOnBean(IdGenerator.class)} 失效
 * @date 10:34
 **/
@AutoConfiguration
@AutoConfigureAfter(SnowflakeAutoConfiguration.class)
public class SpringAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JsonCodec.class)
    public JsonCodec jsonCodec() {
        return JacksonJsonCodec.DEFAULT;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(IdGenerator.class)
    public IdUtils idUtils(IdGenerator idGenerator) {
        return new IdUtils(idGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringEnvironmentHelper springEnvironmentHelper(Environment environment, JsonCodec jsonCodec) {
        return new SpringEnvironmentHelper(environment, jsonCodec);
    }
}
