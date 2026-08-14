package io.github.aicyi.midware.web.annotation;

import java.lang.annotation.*;

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
