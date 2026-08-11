package io.github.aicyi.midware.message.mq.adapter;

import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.lang.type.MessageType;
import io.github.aicyi.commons.lang.model.MessageSendResult;
import io.github.aicyi.midware.message.core.sender.AbstractMessageSender;
import io.github.aicyi.midware.message.mq.model.MqMessage;
import io.github.aicyi.midware.message.mq.sender.MqSender;

/**
 * @author Mr.Min
 * @description MQ消息发送器适配器，对接通用消息框架
 * @date 2025/8/25
 **/
public class MqMessageSender extends AbstractMessageSender {
    private final MqSender mqSender;

    public MqMessageSender(MqSender mqSender) {
        this.mqSender = mqSender;
    }

    @Override
    protected void validate(MessageContent content) {
        if (!supports(content.getMessageType())) {
            throw new UnsupportedOperationException("不支持的消息类型");
        }

        if (!(content instanceof MqMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        MqMessage message = (MqMessage) content;

        if (!message.isValid()) {
            throw new IllegalArgumentException("消息参数错误");
        }
    }

    @Override
    protected MessageSendResult doSend(MessageContent content) throws MessageSendException {

        MqMessage message = (MqMessage) content;

        boolean isSucc;
        // 调用实际的MQ发送服务
        if (message.isDelayed()) {

            isSucc = mqSender.sendDelayed(message.getDestination(), message.getContent(), message.getDelay());
        } else {

            isSucc = mqSender.send(message.getDestination(), message.getContent(), message.getProperties());
        }

        if (!isSucc) {
            throw new MessageSendException("UNKNOWN_ERROR", "发送MQ消息失败");
        }

        return MessageSendResult.success(message.getMessageId(), message.getBusinessId());
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.MQ.equals(messageType);
    }
}
