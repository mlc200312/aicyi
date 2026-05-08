package io.github.aicyi.midware.message.core.template;

import io.github.aicyi.midware.message.core.model.MessageContent;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 抽象模板发送器
 * @date 2026/5/7
 **/
public abstract class AbstractTemplateSender<T extends MessageContent> implements TemplateSender<T> {

    private final TemplateProvider templateProvider;

    protected AbstractTemplateSender(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    public boolean sendTemplate(String templateCode, Map<String, String> templateParams, T message) {

        MessageTemplate template = templateProvider.getTemplate(templateCode);

        validateTemplate(template, templateParams);

        return doSend(template, message);
    }

    protected abstract void validateTemplate(MessageTemplate template, Map<String, String> templateParams);

    protected abstract boolean doSend(MessageTemplate template, T message);
}