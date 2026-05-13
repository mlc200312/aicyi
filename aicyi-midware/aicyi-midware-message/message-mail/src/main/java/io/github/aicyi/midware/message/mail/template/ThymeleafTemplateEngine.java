package io.github.aicyi.midware.message.mail.template;

import io.github.aicyi.commons.core.template.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * @author Mr.Min
 * @description Thymeleaf 模版引擎实现
 * @date 14:14
 **/
public class ThymeleafTemplateEngine implements TemplateEngine {

    //模板引擎
    private org.thymeleaf.TemplateEngine templateEngine;

    public ThymeleafTemplateEngine(org.thymeleaf.TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String process(String template, Map<String, Object> templateParams) {
        Context context = new Context();
        context.setVariables(templateParams);
        return templateEngine.process(template, context);
    }
}
