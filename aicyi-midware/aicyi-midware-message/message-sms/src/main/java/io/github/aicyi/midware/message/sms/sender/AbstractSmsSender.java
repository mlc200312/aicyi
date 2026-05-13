package io.github.aicyi.midware.message.sms.sender;

import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.midware.message.core.template.AbstractTemplateSender;
import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.commons.core.template.TemplateEngine;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.sms.model.SmsMessage;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mr.Min
 * @description 抽象的短信发送者实现
 * @date 09:55
 **/
public abstract class AbstractSmsSender extends AbstractTemplateSender<SmsMessage> implements SmsSender {

    protected final ExecutorService executorService;

    public AbstractSmsSender(TemplateProvider templateProvider, TemplateEngineFactory factory, ExecutorService executorService) {
        super(templateProvider, factory);
        this.executorService = executorService;
    }

    public AbstractSmsSender(TemplateProvider templateProvider, TemplateEngineFactory factory) {
        this(templateProvider, factory, Executors.newFixedThreadPool(5));
    }

    public AbstractSmsSender() {
        this(templateCode -> null, null);
    }


    @Override
    public CompletableFuture<Boolean> sendAsync(List<String> phoneNumbers, String messageContent, String sign) {
        return CompletableFuture.supplyAsync(() -> {
            phoneNumbers.forEach(number -> send(number, messageContent, sign));
            return true;
        }, executorService);
    }


    @Override
    protected boolean doSend(MessageTemplate template, SmsMessage message) {
        try {
            TemplateEngine templateEngine = getTemplateEngine(TemplateEngineType.valueOf(template.getEngineType()));

            String content = templateEngine.process(template.getContent(), message.getTemplateParams());

            message.getPhoneNumbers().forEach(phoneNumber -> send(phoneNumber, content, template.getSignature()));

            return true;
        } catch (MessageSendException e) {

            logger.error(e, "发送模板短信失败 - 手机号: {}, 模板: {}", message.getPhoneNumbers(), template);

            return false;
        }
    }

    /**
     * 关闭线程池资源
     */
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
