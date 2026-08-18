package io.github.aicyi.commons.core.template;

import io.github.aicyi.commons.lang.text.StringFormat;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 基于命名占位符替换（{@link StringFormat}）的简单模板引擎实现，
 * 不支持条件/循环等复杂模板语法，复杂场景请注册 FreeMarker/Thymeleaf 等引擎
 * @date 2025/8/25
 **/
public class DefaultTemplateEngine implements TemplateEngine {

    @Override
    public String process(String template, Map<String, Object> templateParams) {

        return StringFormat.formatNamed(template, templateParams);
    }
}
