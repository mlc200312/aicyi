package io.github.aicyi.commons.core.template;

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
     * @param template       模板文本
     * @param templateParams 模板参数
     * @return 渲染后的文本
     */
    String process(String template, Map<String, Object> templateParams);
}

