package io.github.aicyi.midware.message.core.template;

import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.JsonUtils;
import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.midware.message.core.model.TemplateEngineType;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 抽象模板发送器
 * @date 2026/5/7
 **/
public abstract class AbstractTemplateSender<T extends TemplateMessage> implements TemplateSender<T> {

    protected final static JsonCodec DEFAULT_JSON_CODEC = JsonUtils.getInstance();

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final TemplateProvider templateProvider;
    protected final TemplateEngineFactory factory;

    protected AbstractTemplateSender(TemplateProvider templateProvider, TemplateEngineFactory factory) {
        this.templateProvider = templateProvider;
        this.factory = factory;
    }

    @Override
    public boolean sendTemplate(T message) {

        MessageTemplate template = templateProvider.getTemplate(message.getTemplateId());

        validateTemplate(template, message.getTemplateParams());

        return doSend(template, message);
    }

    protected void validateTemplate(MessageTemplate template, Map<String, Object> templateParams) {

        if (template == null) {
            throw new MessageSendException("NOT_FOUND_TEMPLATE", "模版不存在");
        }

        List<String> required = DEFAULT_JSON_CODEC.fromJsonList(template.getVariables(), String.class);

        for (String key : required) {
            if (templateParams == null || !templateParams.containsKey(key)) {
                throw new IllegalArgumentException("缺少变量：" + key);
            }
        }
    }

    protected TemplateEngine getTemplateEngine(TemplateEngineType engineType) {

        TemplateEngine templateEngine = factory.getTemplateEngine(engineType);

        if (templateEngine == null) {
            logger.error("未配置模板引擎，无法发送模板邮件");
            throw new MessageSendException("NOT_FOUND_TEMPLATE_ENGINE", "模版引擎不存在");
        }

        return templateEngine;
    }

    protected abstract boolean doSend(MessageTemplate template, T message);
}