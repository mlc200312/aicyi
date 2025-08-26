package com.aicyiframework.core.message;

import com.aicyiframework.core.exception.MessageSendException;
import com.aicyiframework.core.sms.SmsManager;

/**
 * @author Mr.Min
 * @description Email发送器实现
 * @date 2025/8/25
 **/
public class SmsMessageSender extends AbstractMessageSender {
    private SmsManager smsManager; // 假设的邮件服务

    public SmsMessageSender(SmsManager smsManager) {
        this.smsManager = smsManager;
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.SMS.equals(messageType);
    }

    @Override
    protected SendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof SmsMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        SmsMessage message = (SmsMessage) content;

        // 调用实际的短信发送服务
        boolean isSend = smsManager.sendSms(message.getPhoneNumbers(), message.getContent(), message.getTemplateId(), message.getTemplateParams(), message.getSignName());

        if (isSend) {
            return SendResult.success(message.getMessageId(), message.getBusinessId());
        }

        return SendResult.failure("SEND SMS FAIL");
    }
}