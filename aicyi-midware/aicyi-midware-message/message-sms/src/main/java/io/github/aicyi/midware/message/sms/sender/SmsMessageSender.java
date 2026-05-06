package io.github.aicyi.midware.message.sms.sender;


import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.model.MessageType;
import io.github.aicyi.midware.message.core.model.MessageSendResult;
import io.github.aicyi.midware.message.core.sender.AbstractMessageSender;
import io.github.aicyi.midware.message.sms.model.SmsMessage;

/**
 * @author Mr.Min
 * @description Email发送器实现
 * @date 2025/8/25
 **/
public class SmsMessageSender extends AbstractMessageSender {
    private SmsSender smsSender; // 假设的邮件服务

    public SmsMessageSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    @Override
    protected void validate(MessageContent content) {
        if (!supports(content.getMessageType())) {
            throw new UnsupportedOperationException("不支持的消息类型");
        }

        if (!(content instanceof SmsMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        SmsMessage message = (SmsMessage) content;

        if (!message.isValid()) {
            throw new IllegalArgumentException("消息参数错误");
        }
    }

    @Override
    protected MessageSendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof SmsMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        SmsMessage message = (SmsMessage) content;

        // 调用实际的短信发送服务
        message.getPhoneNumbers().forEach(number -> {
            if (message.isContentMessage()) {
                smsSender.send(number, message.getContent(), message.getSignName());
            } else {
                smsSender.sendTemplate(number, message.getTemplateId(), message.getTemplateParams(), message.getSignName());
            }
        });

        return MessageSendResult.success(message.getMessageId(), message.getBusinessId());
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.SMS.equals(messageType);
    }
}