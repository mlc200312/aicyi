package io.github.aicyi.midware.message.sms.adapter;


import io.github.aicyi.midware.message.core.exception.MessageResultCode;
import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.core.message.MessageType;
import io.github.aicyi.commons.core.message.MessageSendResult;
import io.github.aicyi.midware.message.core.sender.AbstractMessageSender;
import io.github.aicyi.midware.message.sms.model.SmsMessage;
import io.github.aicyi.midware.message.sms.sender.SmsSender;

/**
 * @author Mr.Min
 * @description 短信消息发送器适配器，对接通用消息框架
 * @date 2025/8/25
 **/
public class SmsMessageSender extends AbstractMessageSender {
    private final SmsSender smsSender; // 短信服务

    public SmsMessageSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    @Override
    protected void validate(MessageContent<?> content) {
        if (!supports(content.getMessageType())) {
            throw new MessageSendException(MessageResultCode.MESSAGE_NOT_SUPPORTED,
                    "不支持的消息类型: " + content.getMessageType());
        }

        if (!(content instanceof SmsMessage)) {
            throw new MessageSendException(MessageResultCode.MESSAGE_PARAM_ERROR,
                    "不支持的消息类型: " + content.getClass().getSimpleName());
        }

        SmsMessage message = (SmsMessage) content;

        if (!message.isValid()) {
            throw new MessageSendException(MessageResultCode.MESSAGE_PARAM_ERROR, "消息参数错误");
        }
    }

    @Override
    protected MessageSendResult doSend(MessageContent<?> content) throws MessageSendException {

        SmsMessage message = (SmsMessage) content;

        // 调用实际的短信发送服务
        if (message.isContentMessage()) {

            message.getPhoneNumbers().forEach(number -> {

                smsSender.send(number, message.getContent(), message.getSign());
            });
        } else {

            smsSender.sendTemplate(message);
        }

        return MessageSendResult.success(message.getMessageId(), message.getBusinessId());
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.SMS.equals(messageType);
    }
}
