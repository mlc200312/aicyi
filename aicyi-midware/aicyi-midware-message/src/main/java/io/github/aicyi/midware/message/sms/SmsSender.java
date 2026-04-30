package io.github.aicyi.midware.message.sms;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 短信发送服务
 * @date 2025/8/25
 **/
public interface SmsSender {
    /**
     * 发送简单文本短信
     *
     * @param phoneNumber
     * @param messageContent
     * @param signName
     * @return
     * @
     */
    boolean send(String phoneNumber, String messageContent, String signName);

    /**
     * 发送模板短信
     *
     * @param phoneNumber
     * @param templateId
     * @param templateParams
     * @param signName
     * @return
     * @
     */
    boolean sendTemplate(String phoneNumber, String templateId, Map<String, String> templateParams, String signName);

    /**
     * 异步发送短信
     *
     * @param phoneNumbers
     * @param messageContent
     * @param signName
     * @return
     * @
     */
    CompletableFuture<Boolean> sendAsync(List<String> phoneNumbers, String messageContent, String signName);

    /**
     * 异步发送短信
     *
     * @param phoneNumber
     * @param templateId
     * @param templateVariables
     * @param signName
     * @return
     * @
     */
    CompletableFuture<Boolean> sendTemplateAsync(List<String> phoneNumber, String templateId, Map<String, String> templateVariables, String signName);
}
