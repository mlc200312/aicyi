package com.aicyiframework.core.message;

/**
 * @author Mr.Min
 * @description 统一消息服务实现
 * @date 16:07
 **/
public class DefaultUnifiedMessageManager implements UnifiedMessageManager {
    private final MessageSenderFactory senderFactory;

    public DefaultUnifiedMessageManager(MessageSenderFactory senderFactory) {
        this.senderFactory = senderFactory;
    }

    @Override
    public SendResult send(MessageContent content) {
        MessageSender sender = senderFactory.getSender(content.getMessageType());
        return sender.send(content);
    }

    @Override
    public void sendAsync(MessageContent content, SendCallback callback) {
        MessageSender sender = senderFactory.getSender(content.getMessageType());
        sender.sendAsync(content, callback);
    }

    @Override
    public SendResult send(MessageContent content, MessagePriority priority) {
        // 根据优先级处理消息，这里可以根据需要实现优先级逻辑
        return send(content);
    }
}
