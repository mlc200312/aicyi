package io.github.aicyi.midware.message.core.template;

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
     * @param template
     * @param templateParams
     * @return
     */
    String process(String template, Map<String, Object> templateParams);
}

