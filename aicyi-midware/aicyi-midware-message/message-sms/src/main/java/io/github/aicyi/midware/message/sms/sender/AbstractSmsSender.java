package io.github.aicyi.midware.message.sms.sender;

import io.github.aicyi.commons.lang.JsonCodec;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.midware.message.core.template.AbstractTemplateSender;
import io.github.aicyi.midware.message.core.template.MessageTemplate;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.sms.template.DefaultTemplateRender;
import io.github.aicyi.midware.message.sms.template.TemplateRender;
import io.github.aicyi.midware.message.sms.model.SmsMessage;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 09:55
 **/
public abstract class AbstractSmsSender extends AbstractTemplateSender<SmsMessage> implements SmsSender {

    protected final static JsonCodec DEFAULT_JSON_CODEC = JacksonJsonCodec.DEFAULT;
    protected final static TemplateRender DEFAULT_TEMPLATE_RENDER = new DefaultTemplateRender();

    protected final TemplateRender templateRender;
    protected final JsonCodec jsonCodec;
    protected final ExecutorService executorService;

    public AbstractSmsSender(TemplateProvider templateProvider, TemplateRender templateRender, JsonCodec jsonCodec, ExecutorService executorService) {
        super(templateProvider);
        this.templateRender = templateRender;
        this.jsonCodec = jsonCodec;
        this.executorService = executorService;
    }

    public AbstractSmsSender(TemplateProvider templateProvider) {
        this(templateProvider, DEFAULT_TEMPLATE_RENDER, DEFAULT_JSON_CODEC, Executors.newFixedThreadPool(5));
    }

    public AbstractSmsSender() {
        this(templateCode -> null);
    }

    @Override
    protected void validateTemplate(MessageTemplate template, Map<String, String> templateParams) {
        if (template == null) {
            throw new MessageSendException("NOT_FOUND_TEMPLATE", "模版不存在");
        }

        List<String> required = jsonCodec.fromJsonList(template.getVariables(), String.class);

        for (String key : required) {
            if (templateParams == null || !templateParams.containsKey(key)) {
                throw new IllegalArgumentException("缺少变量：" + key);
            }
        }
    }

    @Override
    protected boolean doSend(MessageTemplate template, SmsMessage message) {

        String content = templateRender.render(template.getContent(), message.getTemplateParams());

        message.getPhoneNumbers().forEach(phoneNumber -> send(phoneNumber, content, message.getSign()));

        return true;
    }

    @Override
    public boolean sendTemplate(String phoneNumber, String templateId, Map<String, String> templateParams, String sign) {

        return sendTemplate(templateId, templateParams, SmsMessage.of(phoneNumber, templateId, templateParams, sign));
    }

    @Override
    public CompletableFuture<Boolean> sendAsync(List<String> phoneNumbers, String messageContent, String sign) {
        return CompletableFuture.supplyAsync(() -> {
            phoneNumbers.forEach(number -> send(number, messageContent, sign));
            return true;
        }, executorService);
    }


    @Override
    public CompletableFuture<Boolean> sendTemplateAsync(List<String> phoneNumbers, String templateId, Map<String, String> templateParams, String sign) {
        return CompletableFuture.supplyAsync(() -> {
            phoneNumbers.forEach(number -> sendTemplate(number, templateId, templateParams, sign));
            return true;
        }, executorService);
    }

    /**
     * 关闭线程池资源
     */
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
