package io.github.aicyi.midware.web;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * @description  @ResponseBody继承类
 * @author  Mr.Min
 * @date  2021/5/2
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ResponseBody
public @interface ResultControllerAdvice {

}