package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.model.MessagePriority;
import io.github.aicyi.midware.message.core.model.MessageSendResult;

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
    public MessageSendResult send(MessageContent content) {
        MessageSender sender = senderFactory.getSender(content.getMessageType());
        return sender.send(content);
    }

    @Override
    public void sendAsync(MessageContent content, MessageSendCallback callback) {
        MessageSender sender = senderFactory.getSender(content.getMessageType());
        sender.sendAsync(content, callback);
    }

    @Override
    public MessageSendResult send(MessageContent content, MessagePriority priority) {
        // 根据优先级处理消息，这里可以根据需要实现优先级逻辑
        return send(content);
    }
}
