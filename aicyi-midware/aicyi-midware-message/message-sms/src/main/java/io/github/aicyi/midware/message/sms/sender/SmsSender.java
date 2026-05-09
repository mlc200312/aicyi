package io.github.aicyi.midware.message.sms.sender;

import io.github.aicyi.midware.message.core.template.TemplateSender;
import io.github.aicyi.midware.message.sms.model.SmsMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 短信发送服务
 * @date 2025/8/25
 **/
public interface SmsSender extends TemplateSender<SmsMessage> {
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
     * 异步发送短信
     *
     * @param phoneNumbers
     * @param messageContent
     * @param sign
     * @return
     * @
     */
    CompletableFuture<Boolean> sendAsync(List<String> phoneNumbers, String messageContent, String sign);
}
