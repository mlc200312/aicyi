package io.github.aicyi.midware.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mr.Min
 * @description 免鉴权注解
 * <p>
 * 标注在 Controller 类或方法上，对应接口跳过身份验证拦截（无需携带 Bearer Token）
 **/
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {
}
