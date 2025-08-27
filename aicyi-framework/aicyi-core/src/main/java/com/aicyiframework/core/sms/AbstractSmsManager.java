package com.aicyiframework.core.sms;

import com.aicyiframework.core.exception.MessageSendException;

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
public abstract class AbstractSmsManager implements SmsManager {
    protected final ExecutorService executorService;
    protected final Map<String, String> template;

    public AbstractSmsManager(ExecutorService executorService, Map<String, String> template) {
        this.executorService = executorService;
        this.template = template;
    }

    public AbstractSmsManager(Map<String, String> template) {
        this(Executors.newFixedThreadPool(5), template);
    }

    @Override
    public boolean sendTemplateSms(String number, String templateId, Map<String, String> templateParams, String signName) {
        if (templateParams.containsKey(templateId)) {
            String content = template.get(templateId);
            return sendTextSms(number, content, signName);
        }
        throw new MessageSendException("NOT_FOUND_TEMPLATE", "模版不存在");
    }

    @Override
    public CompletableFuture<Boolean> sendTextSmsAsync(List<String> phoneNumbers, String content, String signName) {
        return CompletableFuture.supplyAsync(() -> {
            phoneNumbers.forEach(number -> sendTextSms(number, content, signName));
            return true;
        }, executorService);
    }

    @Override
    public CompletableFuture<Boolean> sendTemplateSmsAsync(List<String> phoneNumbers, String templateId, Map<String, String> templateParams, String signName) {
        return CompletableFuture.supplyAsync(() -> {
            phoneNumbers.forEach(number -> sendTemplateSms(number, templateId, templateParams, signName));
            return true;
        }, executorService);
    }

    /**
     * 关闭线程池资源
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
