package io.github.aicyi.midware.web;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * @author Mr.Min
 * @description {@link EnableRestApi} Web MVC 装配配置
 * <p>
 * 负责装配并按序注册拦截器，以及请求体缓存过滤器：
 * <ul>
 *     <li>{@link RequestLogInterceptor}：请求信息日志（先注册，保证鉴权失败时也能记录日志）</li>
 *     <li>{@link AuthInterceptor}：身份验证拦截</li>
 *     <li>{@link CachingRequestBodyFilter}：请求体缓存过滤器，保证请求体在记录日志时可读</li>
 * </ul>
 * @date 2026/8/13
 **/
@Configuration
public class RestApiWebMvcConfiguration implements WebMvcConfigurer, ImportAware {

    private static final int CACHING_FILTER_ORDER = 1;

    private final ObjectProvider<RequestLogInterceptor> requestLogInterceptorProvider;

    private final ObjectProvider<AuthInterceptor> authInterceptorProvider;

    private boolean enableAuth = true;

    private boolean enableRequestLog = true;

    private String[] authExcludePathPatterns = new String[0];

    public RestApiWebMvcConfiguration(ObjectProvider<RequestLogInterceptor> requestLogInterceptorProvider,
                                      ObjectProvider<AuthInterceptor> authInterceptorProvider) {
        this.requestLogInterceptorProvider = requestLogInterceptorProvider;
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
        this.authExcludePathPatterns = annotationAttributes.getStringArray("authExcludePathPatterns");
    }

    /**
     * 请求信息日志拦截器，{@code enableRequestLog = false} 时不注入
     */
    @Bean
    public RequestLogInterceptor requestLogInterceptor() {
        if (!enableRequestLog) {
            return null;
        }
        return new RequestLogInterceptor();
    }

    /**
     * 身份验证拦截器，{@code enableAuth = false} 时不注入
     */
    @Bean
    public AuthInterceptor authInterceptor() {
        if (!enableAuth) {
            return null;
        }
        return new AuthInterceptor();
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

        // 日志拦截器先注册：最先标记开始时间、最后输出日志，鉴权失败也能记录
        RequestLogInterceptor requestLogInterceptor = requestLogInterceptorProvider.getIfAvailable();
        if (requestLogInterceptor != null) {
            registry.addInterceptor(requestLogInterceptor);
        }

        AuthInterceptor authInterceptor = authInterceptorProvider.getIfAvailable();
        if (authInterceptor != null) {
            applyAuthExcludePatterns(registry.addInterceptor(authInterceptor));
        }
    }

    private void applyAuthExcludePatterns(InterceptorRegistration registration) {
        if (authExcludePathPatterns.length > 0) {
            registration.excludePathPatterns(authExcludePathPatterns);
        }
    }
}
