package io.github.aicyi.midware.message.sms;

import io.github.aicyi.commons.core.message.MessageSendException;

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
public abstract class AbstractSmsSender implements SmsSender {
    protected final ExecutorService executorService;
    protected final Map<String, String> template;

    public AbstractSmsSender(ExecutorService executorService, Map<String, String> template) {
        this.executorService = executorService;
        this.template = template;
    }

    public AbstractSmsSender(Map<String, String> template) {
        this(Executors.newFixedThreadPool(5), template);
    }

    @Override
    public boolean sendTemplate(String phoneNumber, String templateId, Map<String, String> templateParams, String signName) {
        if (template.containsKey(templateId)) {
            String content = template.get(templateId);
            return send(phoneNumber, content, signName);
        }
        throw new MessageSendException("NOT_FOUND_TEMPLATE", "模版不存在");
    }

    @Override
    public CompletableFuture<Boolean> sendAsync(List<String> phoneNumbers, String messageContent, String signName) {
        return CompletableFuture.supplyAsync(() -> {
            phoneNumbers.forEach(number -> send(number, messageContent, signName));
            return true;
        }, executorService);
    }

    @Override
    public CompletableFuture<Boolean> sendTemplateAsync(List<String> phoneNumber, String templateId, Map<String, String> templateVariables, String signName) {
        return CompletableFuture.supplyAsync(() -> {
            phoneNumber.forEach(number -> sendTemplate(number, templateId, templateVariables, signName));
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
