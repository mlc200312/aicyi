package io.github.aicyi.midware.message.sms.template;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 模版渲染器
 * @date 15:52
 **/
public interface TemplateRender {

    /**
     * 渲染模板
     *
     * @param content
     * @param templateParams
     * @return
     */
    String render(String content, Map<String, String> templateParams);
}
