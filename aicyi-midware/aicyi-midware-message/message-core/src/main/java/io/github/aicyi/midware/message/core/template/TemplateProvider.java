package io.github.aicyi.midware.message.core.template;

/**
 * @author Mr.Min
 * @description 模版提供者
 * @date 2026/5/7
 **/
public interface TemplateProvider {

    MessageTemplate getTemplate(String templateCode);
}