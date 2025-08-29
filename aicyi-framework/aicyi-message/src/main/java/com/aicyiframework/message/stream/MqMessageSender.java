package com.aicyiframework.message.stream;

import com.aicyiframework.core.message.AbstractMessageSender;
import com.aicyiframework.core.message.MessageContent;
import com.aicyiframework.core.message.MessageType;
import com.aicyiframework.core.message.SendResult;
import com.aicyiframework.core.exception.MessageSendException;

/**
 * @author Mr.Min
 * @description MQ发送器实现
 * @date 2025/8/25
 **/
public class MqMessageSender extends AbstractMessageSender {
    private MqManager mqManager;

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.SMS.equals(messageType);
    }

    @Override
    protected SendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof MqMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        MqMessage message = (MqMessage) content;

        // 调用实际的MQ发送服务
        mqManager.send(message);

        return SendResult.success(message.getMessageId(), message.getBusinessId());
    }
}