package io.github.aicyi.commons.core.template;

import io.github.aicyi.commons.lang.text.StringFormat;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 基于FreeMarker的模板引擎实现
 * @date 2025/8/25
 **/
public class DefualtTemplateEngine implements TemplateEngine {

    @Override
    public String process(String template, Map<String, Object> templateParams) {

        return StringFormat.formatNamed(template, templateParams);
    }
}