package io.github.aicyi.midware.message.sms.template;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 模版渲染管理器实现
 * @date 15:54
 **/
public class DefaultTemplateRender implements TemplateRender {

    @Override
    public String render(String content, Map<String, String> templateParams) {

        String result = content;

        for (Map.Entry<String, String> entry : templateParams.entrySet()) {

            result = result.replace(
                    "${" + entry.getKey() + "}",
                    String.valueOf(entry.getValue())
            );
        }

        return result;
    }
}
