package io.github.aicyi.midware.message.mq;

import io.github.aicyi.commons.core.message.AbstractMessageSender;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.core.message.MessageType;
import io.github.aicyi.commons.core.message.SendResult;
import io.github.aicyi.commons.core.message.MessageSendException;

/**
 * @author Mr.Min
 * @description MQ发送器实现
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
    protected SendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof MqMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        MqMessage message = (MqMessage) content;

        // 调用实际的MQ发送服务
        if (message.isDelayed()) {

            mqSender.sendDelayed(message.getDestination(), message.getContent(), message.getDelay());
        } else {

            mqSender.send(message.getDestination(), message.getContent(), message.getProperties());
        }

        return SendResult.success(message.getMessageId(), message.getBusinessId());
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.MQ.equals(messageType);
    }
}