package io.github.aicyi.midware.autoconfigure;

import io.github.aicyi.commons.core.context.SpringEnvironmentHelper;
import io.github.aicyi.commons.lang.SmartJsonMapper;
import io.github.aicyi.commons.util.jackson.JacksonJsonMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Mr.Min
 * @description Spring自动配置类
 * @date 10:34
 **/
public class SpringAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SmartJsonMapper.class)
    public SmartJsonMapper smartJsonMapper() {
        return JacksonJsonMapper.DEFAULT;
    }

    @Bean
    @ConditionalOnBean(Environment.class)
    public SpringEnvironmentHelper springEnvironmentHelper(Environment environment, SmartJsonMapper smartJsonMapper) {
        return new SpringEnvironmentHelper(environment, smartJsonMapper);
    }
}
