package io.github.aicyi.midware.rabbitmq;

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
    private final MqManager mqManager;

    public MqMessageSender(MqManager mqManager) {
        this.mqManager = mqManager;
    }

    public MqManager getMessageSender() {
        return mqManager;
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.MQ.equals(messageType);
    }

    @Override
    protected SendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof MqMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        MqMessage message = (MqMessage) content;

        // 调用实际的MQ发送服务
        if (message.isDelayed()) {

            mqManager.sendDelayed(message.getDestination(), message.getContent(), message.getDelay());
        } else {

            mqManager.send(message.getDestination(), message.getContent(), message.getProperties());
        }

        return SendResult.success(message.getMessageId(), message.getBusinessId());
    }
}