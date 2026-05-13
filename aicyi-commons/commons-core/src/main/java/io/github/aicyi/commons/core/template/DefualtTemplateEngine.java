package io.github.aicyi.commons.core.template;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 基于FreeMarker的模板引擎实现
 * @date 2025/8/25
 **/
public class DefualtTemplateEngine implements TemplateEngine {

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