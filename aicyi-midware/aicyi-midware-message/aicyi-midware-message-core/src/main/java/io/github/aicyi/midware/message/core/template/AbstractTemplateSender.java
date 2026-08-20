package io.github.aicyi.midware.message.core.template;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.template.TemplateEngine;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.commons.core.template.TemplateRequest;
import io.github.aicyi.commons.core.template.TemplateSender;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.json.JsonUtils;
import io.github.aicyi.midware.message.core.exception.MessageResultCode;
import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.midware.message.core.model.MessageTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 抽象模板发送器
 * @date 2026/5/7
 **/
public abstract class AbstractTemplateSender<T extends TemplateRequest> implements TemplateSender<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final TemplateProvider templateProvider;

    protected final TemplateEngineFactory factory;

    protected AbstractTemplateSender(TemplateProvider templateProvider, TemplateEngineFactory factory) {
        this.templateProvider = templateProvider;
        this.factory = factory;
    }

    @Override
    public boolean sendTemplate(T message) {

        if (templateProvider == null) {
            // 未配置模板服务（如未开启 aicyi.message.template）时给出明确错误而非 NPE
            throw new MessageSendException(MessageResultCode.TEMPLATE_NOT_FOUND, "模版服务未配置");
        }

        MessageTemplate template = templateProvider.getTemplate(message.getTemplateId());

        validateTemplate(template, message.getTemplateParams());

        return doSend(template, message);
    }

    protected void validateTemplate(MessageTemplate template, Map<String, Object> templateParams) {

        if (template == null) {
            throw new MessageSendException(MessageResultCode.TEMPLATE_NOT_FOUND, "模版不存在");
        }

        List<String> required = JsonUtils.getInstance().fromJsonList(template.getVariables(), String.class);

        for (String key : required) {
            if (templateParams == null || !templateParams.containsKey(key)) {
                throw new MessageSendException(MessageResultCode.MESSAGE_PARAM_ERROR, "缺少变量：" + key);
            }
        }
    }

    protected TemplateEngine getTemplateEngine(TemplateEngineType engineType) {

        if (factory == null) {
            logger.error("未配置模板引擎工厂，无法发送模板消息");
            throw new MessageSendException(MessageResultCode.TEMPLATE_ENGINE_NOT_FOUND, "模版引擎工厂未配置");
        }

        try {
            return factory.getTemplateEngine(engineType);
        } catch (IllegalArgumentException e) {
            // 工厂实现对未注册引擎直接抛异常，此处统一收敛为消息模块错误码
            logger.error("未配置模板引擎 {}，无法发送模板消息", engineType);
            throw new MessageSendException(MessageResultCode.TEMPLATE_ENGINE_NOT_FOUND,
                    "模版引擎不存在: " + engineType);
        }
    }

    protected abstract boolean doSend(MessageTemplate template, T message);
}