package io.github.aicyi.midware.message.mail.template;


import freemarker.template.Configuration;
import io.github.aicyi.commons.core.template.TemplateEngine;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 基于FreeMarker的模板引擎实现
 * @date 2025/8/25
 **/
public class DefualtMailTemplateEngine implements TemplateEngine {
    private final Configuration configuration;

    public DefualtMailTemplateEngine() {
        configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setDefaultEncoding("UTF-8");
    }

    @Override
    public String process(String template, Map<String, Object> templateParams) {
        return template;
    }
}