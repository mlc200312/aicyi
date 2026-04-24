package io.github.aicyi.example.boot.config;

import io.github.aicyi.commons.core.token.DefaultTokenConfig;
import io.github.aicyi.commons.core.token.TokenConfig;
import io.github.aicyi.commons.core.token.TokenManager;
import io.github.aicyi.commons.lang.IJWTInfo;
import io.github.aicyi.midware.redis.jwt.RedisJwtTokenManager;
import io.github.aicyi.midware.web.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Mr.Min
 * @description Web相关配置
 * @date 11:48
 **/
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final RedisConnectionFactory redisConnectionFactory;

    public WebConfiguration(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthInterceptor(getStringIJWTInfoTokenManager())).excludePathPatterns("/webjars/**", "/v2/api-docs");
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
    public TokenManager<String, IJWTInfo> getStringIJWTInfoTokenManager() {
        TokenConfig tokenConfig = DefaultTokenConfig.builder()
                .signingKey("LcR6QUhqWrDqK1InQDKlpZuKx6X/ZgEISdFpKwO3i/E=")
                .multiTokenAllowed(true)
                .build();
        return new RedisJwtTokenManager(tokenConfig, redisConnectionFactory);
    }

    @Bean
    public AuthInterceptor getAuthInterceptor(TokenManager<String, IJWTInfo> tokenManager) {
        return new AuthInterceptor(tokenManager);
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
}
