package io.github.aicyi.midware.message.mail;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 简单的模板引擎接口
 * @date 2025/8/25
 **/
public interface TemplateEngine {
    /**
     * 处理模版
     *
     * @param templateName
     * @param variables
     * @return
     */
    String process(String templateName, Map<String, Object> variables);
}

