package io.github.aicyi.midware.web.config;

import io.github.aicyi.midware.web.auth.AuthInterceptor;
import io.github.aicyi.midware.web.log.RequestLogInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Mr.Min
 * @description Web MVC 配置
 * <p>
 * 按序注册拦截器，拦截器 Bean 由 {@link RestApiConfigurationRegistrar} 按 {@code @EnableRestApi} 属性条件注册：
 * <ul>
 *     <li>{@link RequestLogInterceptor}：请求信息日志（先注册，保证鉴权失败时也能记录日志）</li>
 *     <li>{@link AuthInterceptor}：身份验证拦截</li>
 * </ul>
 * @date 2026/8/13
 **/
public class RestApiWebMvcConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<RequestLogInterceptor> requestLogInterceptorProvider;

    private final ObjectProvider<AuthInterceptor> authInterceptorProvider;

    private final String[] authExcludePathPatterns;

    public RestApiWebMvcConfiguration(ObjectProvider<RequestLogInterceptor> requestLogInterceptorProvider,
                                      ObjectProvider<AuthInterceptor> authInterceptorProvider,
                                      String[] authExcludePathPatterns) {
        this.requestLogInterceptorProvider = requestLogInterceptorProvider;
        this.authInterceptorProvider = authInterceptorProvider;
        this.authExcludePathPatterns = authExcludePathPatterns != null ? authExcludePathPatterns : new String[0];
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
