package com.aicyiframework.core.message;

import com.aicyiframework.core.exception.MessageSendException;

/**
 * @author Mr.Min
 * @description Email发送器实现
 * @date 2025/8/25
 **/
public class SmsMessageSender extends AbstractMessageSender {
    private SmsManager smsManager; // 假设的邮件服务

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.SMS.equals(messageType);
    }

    @Override
    protected SendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof EmailMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        SmsMessage message = (SmsMessage) content;

        // 调用实际的短信发送服务
        smsManager.send(message.getPhoneNumbers(), message.getTemplateCode(), message.getTemplateParams(), message.getSignName());

        return SendResult.success(message.getMessageId(), message.getBusinessId());
    }
}