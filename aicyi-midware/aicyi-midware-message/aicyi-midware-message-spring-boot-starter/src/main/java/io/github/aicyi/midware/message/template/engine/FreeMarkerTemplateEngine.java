package io.github.aicyi.midware.message.template.engine;


import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.aicyi.commons.core.template.TemplateEngine;
import io.github.aicyi.midware.message.core.exception.MessageResultCode;
import io.github.aicyi.midware.message.core.exception.MessageSendException;

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
    }

    public FreeMarkerTemplateEngine(String basePackagePath) {
        this();
        configuration.setTemplateLoader(
                new ClassTemplateLoader(
                        this.getClass(),
                        basePackagePath
                )
        );
    }

    @Override
    public String process(String template, Map<String, Object> templateParams) {
        try {
            Template freemarkerTemplate;

            if (template.endsWith(".ftl")) {

                freemarkerTemplate = configuration.getTemplate(template);
            } else {

                freemarkerTemplate = new Template("template", template, configuration);
            }

            StringWriter writer = new StringWriter();

            freemarkerTemplate.process(templateParams, writer);

            return writer.toString();
        } catch (Exception e) {
            // 异常消息仅携带模板标识，避免大段模板内容随异常外泄
            throw new MessageSendException(MessageResultCode.TEMPLATE_RENDER_ERROR, "模板渲染失败", e);
        }
    }
}
