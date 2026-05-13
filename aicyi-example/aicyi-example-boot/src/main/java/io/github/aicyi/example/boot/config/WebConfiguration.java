package io.github.aicyi.example.boot.config;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.token.TokenService;
import io.github.aicyi.example.domain.UserInfo;
import io.github.aicyi.midware.redis.token.RedisTokenService;
import io.github.aicyi.midware.web.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    private final RedisTokenService<IJWTInfo> tokenService;

    public WebConfiguration(RedisTokenService<IJWTInfo> tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        AuthInterceptor authInterceptor = getAuthInterceptor(tokenService);

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
    public AuthInterceptor getAuthInterceptor(TokenService<String, IJWTInfo> tokenService) {
        return new AuthInterceptor(tokenService);
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
