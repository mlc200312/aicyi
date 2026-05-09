package io.github.aicyi.midware.message.sms.template;

import io.github.aicyi.midware.message.core.template.TemplateEngine;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 模版渲染管理器实现
 * @date 15:54
 **/
public class DefaultSmsTemplateEngine implements TemplateEngine {

    @Override
    public String process(String template, Map<String, Object> templateParams) {
        String result = template;
        for (Map.Entry<String, Object> entry : templateParams.entrySet()) {
            result = result.replace(
                    "${" + entry.getKey() + "}",
                    String.valueOf(entry.getValue())
            );
        }
        return result;
    }
}
