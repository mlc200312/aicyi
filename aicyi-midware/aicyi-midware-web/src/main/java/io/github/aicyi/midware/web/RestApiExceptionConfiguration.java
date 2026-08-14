package io.github.aicyi.midware.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * @author Mr.Min
 * @description {@link EnableRestApi} 异常处理装配配置
 * <p>
 * 负责装配 {@link GlobalExceptionHandler} 全局异常处理器，统一异常响应并记录异常请求日志
 * @date 2026/8/13
 **/
@Configuration
public class RestApiExceptionConfiguration implements ImportAware {

    private boolean enableGlobalExceptionHandler = true;

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {
        Map<String, Object> attributes = importMetadata.getAnnotationAttributes(EnableRestApi.class.getName());
        if (attributes == null) {
            return;
        }
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(attributes);
        this.enableGlobalExceptionHandler = annotationAttributes.getBoolean("enableGlobalExceptionHandler");
    }

    /**
     * 全局异常处理器，{@code enableGlobalExceptionHandler = false} 时不注入
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        if (!enableGlobalExceptionHandler) {
            return null;
        }
        return new GlobalExceptionHandler();
    }
}
