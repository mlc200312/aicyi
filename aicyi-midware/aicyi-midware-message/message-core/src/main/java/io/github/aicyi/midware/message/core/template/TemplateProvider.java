package io.github.aicyi.midware.message.core.template;

import io.github.aicyi.midware.message.core.model.MessageTemplate;

/**
 * @author Mr.Min
 * @description 模版提供者
 * @date 2026/5/7
 **/
public interface TemplateProvider {

    /**
     * 获取模版
     *
     * @param templateCode
     * @return
     */
    MessageTemplate getTemplate(String templateCode);
}