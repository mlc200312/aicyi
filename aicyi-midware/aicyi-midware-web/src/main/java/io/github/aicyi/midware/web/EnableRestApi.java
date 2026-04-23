package io.github.aicyi.midware.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Mr.Min
 * @description Web配置注解
 * @date 2020-02-19
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({GlobalExceptionHandler.class})
public @interface EnableRestApi {
}
