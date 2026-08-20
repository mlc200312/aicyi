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
 * 按序注册拦截器，拦截器 Bean 由 {@link MidwareWebConfigurationRegistrar} 按 {@code @EnableMidwareWeb} 属性条件注册：
 * <ul>
 *     <li>{@link RequestLogInterceptor}：请求信息日志（先注册，保证鉴权失败时也能记录日志）</li>
 *     <li>{@link AuthInterceptor}：身份验证拦截</li>
 * </ul>
 * @date 2026/8/13
 **/
public class MidwareWebMvcConfiguration implements WebMvcConfigurer {

    private final ObjectProvider<RequestLogInterceptor> requestLogInterceptorProvider;

    private final ObjectProvider<AuthInterceptor> authInterceptorProvider;

    private final String[] excludePathPatterns;

    public MidwareWebMvcConfiguration(ObjectProvider<RequestLogInterceptor> requestLogInterceptorProvider,
                                      ObjectProvider<AuthInterceptor> authInterceptorProvider,
                                      String[] excludePathPatterns) {
        this.requestLogInterceptorProvider = requestLogInterceptorProvider;
        this.authInterceptorProvider = authInterceptorProvider;
        this.excludePathPatterns = excludePathPatterns != null ? excludePathPatterns : new String[0];
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {

        // 日志拦截器先注册：最先标记开始时间、最后输出日志，鉴权失败也能记录
        RequestLogInterceptor requestLogInterceptor = requestLogInterceptorProvider.getIfAvailable();
        if (requestLogInterceptor != null) {
            applyExcludePatterns(registry.addInterceptor(requestLogInterceptor));
        }

        AuthInterceptor authInterceptor = authInterceptorProvider.getIfAvailable();
        if (authInterceptor != null) {
            applyExcludePatterns(registry.addInterceptor(authInterceptor));
        }
    }

    /**
     * 排除路径同时作用于鉴权拦截器与请求日志拦截器（见 {@code EnableMidwareWeb#excludePathPatterns}）
     */
    private void applyExcludePatterns(InterceptorRegistration registration) {
        if (excludePathPatterns.length > 0) {
            registration.excludePathPatterns(excludePathPatterns);
        }
    }
}
