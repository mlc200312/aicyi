package io.github.aicyi.midware.autoconfigure;

import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import io.github.aicyi.midware.context.SpringEnvironmentHelper;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.util.JsonUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Mr.Min
 * @description Spring自动配置类
 * @date 10:34
 **/
@AutoConfiguration
public class SpringAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JsonCodec.class)
    public JsonCodec getDefaultJsonCodec() {
        return JacksonJsonCodec.DEFAULT;
    }

    @Bean
    @ConditionalOnBean(Environment.class)
    public SpringEnvironmentHelper springEnvironmentHelper(Environment environment, JsonCodec jsonCodec) {
        return new SpringEnvironmentHelper(environment, jsonCodec);
    }
}
