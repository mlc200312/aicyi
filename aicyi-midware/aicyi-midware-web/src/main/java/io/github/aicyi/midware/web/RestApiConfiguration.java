package io.github.aicyi.midware.web;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import io.github.aicyi.commons.logging.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * @author Mr.Min
 * @description {@link EnableRestApi} 对应的装配配置
 * <p>
 * 根据注解属性按需装配：
 * <ul>
 *     <li>{@link GlobalExceptionHandler}：全局异常处理器</li>
 *     <li>{@link AuthenticationTokenServiceRegistrar}：将容器中的 Token 服务注册到 AuthenticationTokens 工具</li>
 *     <li>{@link AuthInterceptor}：身份验证拦截器（含请求开始时间标记与请求日志输出）</li>
 *     <li>{@link CachingRequestBodyFilter}：请求体缓存过滤器，保证请求体在记录日志时可读</li>
 * </ul>
 * @date 2026/8/13
 **/
@Configuration
public class RestApiConfiguration implements WebMvcConfigurer, ImportAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiConfiguration.class);

    /**
     * 请求体缓存过滤器顺序，需早于业务过滤器执行
     */
    private static final int CACHING_FILTER_ORDER = 1;

    private final ObjectProvider<AuthInterceptor> authInterceptorProvider;

    private boolean enableAuth = true;

    private boolean enableRequestLog = true;

    private String[] excludePathPatterns = new String[0];

    public RestApiConfiguration(ObjectProvider<AuthInterceptor> authInterceptorProvider) {
        this.authInterceptorProvider = authInterceptorProvider;
    }

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {
        Map<String, Object> attributes = importMetadata.getAnnotationAttributes(EnableRestApi.class.getName());
        if (attributes == null) {
            return;
        }
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(attributes);
        this.enableAuth = annotationAttributes.getBoolean("enableAuth");
        this.enableRequestLog = annotationAttributes.getBoolean("enableRequestLog");
        this.excludePathPatterns = annotationAttributes.getStringArray("excludePathPatterns");
    }

    /**
     * 全局异常处理器，统一异常响应并记录异常请求日志
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * Token 服务注册器，将容器中的 {@code AuthenticationTokenService} Bean 注册到 AuthenticationTokens 工具，
     * 供拦截器与业务代码静态调用；容器中不存在该 Bean 时静默跳过
     */
    @Bean
    public AuthenticationTokenServiceRegistrar authenticationTokenServiceRegistrar(ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider) {
        return new AuthenticationTokenServiceRegistrar(tokenServiceProvider);
    }

    /**
     * 身份验证拦截器
     * <p>
     * 通过 AuthenticationTokens 工具完成鉴权，未开启鉴权或 Token 服务未注册时降级为仅记录请求日志
     */
    @Bean
    public AuthInterceptor authInterceptor() {
        if (!enableAuth && !enableRequestLog) {
            return null;
        }

        if (enableAuth) {
            LOGGER.info("auth interceptor enabled, token service resolved via AuthenticationTokens at runtime");
        }

        return new AuthInterceptor(enableRequestLog);
    }

    /**
     * 请求体缓存过滤器，使请求体在记录日志等任意阶段可重复读取
     */
    @Bean
    public FilterRegistrationBean<CachingRequestBodyFilter> cachingRequestBodyFilter() {
        FilterRegistrationBean<CachingRequestBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingRequestBodyFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(CACHING_FILTER_ORDER);
        return registrationBean;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {

        AuthInterceptor interceptor = authInterceptorProvider.getIfAvailable();
        if (interceptor == null) {
            return;
        }

        if (excludePathPatterns.length > 0) {
            registry.addInterceptor(interceptor).excludePathPatterns(excludePathPatterns);
        } else {
            registry.addInterceptor(interceptor);
        }
    }
}
