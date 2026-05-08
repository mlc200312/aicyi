package io.github.aicyi.midware.message.sms.sender;

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
     * @param sign
     * @return
     * @
     */
    boolean send(String phoneNumber, String messageContent, String sign);

    /**
     * 发送模板短信
     *
     * @param phoneNumber
     * @param templateId
     * @param templateParams
     * @param sign
     * @return
     */
    boolean sendTemplate(String phoneNumber, String templateId, Map<String, String> templateParams, String sign);

    /**
     * 异步发送短信
     *
     * @param phoneNumbers
     * @param messageContent
     * @param sign
     * @return
     * @
     */
    CompletableFuture<Boolean> sendAsync(List<String> phoneNumbers, String messageContent, String sign);

    /**
     * 异步发送短信
     *
     * @param phoneNumbers
     * @param templateId
     * @param templateParams
     * @param sign
     * @return
     */
    CompletableFuture<Boolean> sendTemplateAsync(List<String> phoneNumbers, String templateId, Map<String, String> templateParams, String sign);
}
