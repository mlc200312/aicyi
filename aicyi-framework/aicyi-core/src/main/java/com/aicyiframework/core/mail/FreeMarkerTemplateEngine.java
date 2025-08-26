package com.aicyiframework.core.mail;


import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 基于FreeMarker的模板引擎实现
 * @date 2025/8/25
 **/
public class FreeMarkerTemplateEngine implements TemplateEngine {
    private final Configuration configuration;

    public FreeMarkerTemplateEngine() {
        configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassForTemplateLoading(getClass(), "/templates/email/");
    }

    @Override
    public String process(String templateName, Map<String, Object> variables) {
        try {
            Template template = configuration.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(variables, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("模板渲染失败: " + templateName, e);
        }
    }
}