package io.github.aicyi.midware.web;

import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * @author Mr.Min
 * @description {@link EnableRestApi} 认证装配配置
 * <p>
 * 负责装配 {@link AuthenticationTokenServiceRegistrar}，
 * 将容器中的 Token 服务注册到 AuthenticationTokens 工具，供拦截器与业务代码静态调用
 * @date 2026/8/13
 **/
@Configuration
public class RestApiAuthenticationConfiguration implements ImportAware {

    private boolean enableAuth = true;

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {
        Map<String, Object> attributes = importMetadata.getAnnotationAttributes(EnableRestApi.class.getName());
        if (attributes == null) {
            return;
        }
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(attributes);
        this.enableAuth = annotationAttributes.getBoolean("enableAuth");
    }

    /**
     * Token 服务注册器，{@code enableAuth = false} 时不注入；容器中不存在该 Bean 时静默跳过
     */
    @Bean
    public AuthenticationTokenServiceRegistrar authenticationTokenServiceRegistrar(ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider) {
        if (!enableAuth) {
            return null;
        }
        return new AuthenticationTokenServiceRegistrar(tokenServiceProvider);
    }
}
