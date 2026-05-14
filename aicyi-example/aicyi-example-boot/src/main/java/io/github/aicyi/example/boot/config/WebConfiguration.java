package io.github.aicyi.example.boot.config;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import io.github.aicyi.commons.core.token.JwtPrincipalSerializer;
import io.github.aicyi.commons.core.token.TokenService;
import io.github.aicyi.commons.security.token.JwtRefreshAuthenticationTokenService;
import io.github.aicyi.commons.security.token.jwt.DefultJwtPrincipalSerializer;
import io.github.aicyi.commons.security.token.jwt.JwtTokenProvider;
import io.github.aicyi.example.domain.UserInfo;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import io.github.aicyi.midware.redis.token.MultiRedisTokenServiceImpl;
import io.github.aicyi.midware.web.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Web相关配置
 * @date 11:48
 **/
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final EnhancedRedisTemplateFactory factory;

    public WebConfiguration(EnhancedRedisTemplateFactory factory) {
        this.factory = factory;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        AuthInterceptor authInterceptor = getAuthInterceptor();

        registry.addInterceptor(authInterceptor).excludePathPatterns("/webjars/**", "/v2/api-docs");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/webjars/");
        registry.addResourceHandler("/api-doc.html")
                .addResourceLocations("classpath:/api-doc.html");
    }

    @Bean
    public AuthInterceptor getAuthInterceptor() {
        return new AuthInterceptor(tokenService());
    }

    @Bean
    public CorsFilter apiCrossFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    public AuthenticationTokenService<IJWTInfo> tokenService() {

        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("OczHbdKy3tzPx2PdYw5FwyQALsEZ36jd0Vrj3ZWZ3ic=", "aicyi", "aicyi.com");

        TokenService<String, IJWTInfo> refreshTokenService = new MultiRedisTokenServiceImpl<>(factory, UserInfo.class);

        JwtPrincipalSerializer<IJWTInfo> jwtPrincipalSerializer = new DefultJwtPrincipalSerializer<>(UserInfo.class);

        return new JwtRefreshAuthenticationTokenService<>(jwtTokenProvider, refreshTokenService, jwtPrincipalSerializer, 30, TimeUnit.MINUTES, 7, TimeUnit.DAYS);
    }
}
