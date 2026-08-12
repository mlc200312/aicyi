package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.core.message.MessageSendCallback;
import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.lang.model.MessageSendResult;
import io.github.aicyi.midware.message.core.model.MessagePriority;

import java.util.Objects;

/**
 * @author Mr.Min
 * @description 统一消息服务实现
 * @date 16:07
 **/
public class DefaultUnifiedMessageManager implements UnifiedMessageManager {

    private final MessageSenderFactory senderFactory;

    /**
     * 构造器
     *
     * @param senderFactory 消息发送器工厂，不能为 null
     */
    public DefaultUnifiedMessageManager(MessageSenderFactory senderFactory) {
        this.senderFactory = Objects.requireNonNull(senderFactory, "消息发送器工厂不能为 null");
    }

    @Override
    public MessageSendResult send(MessageContent content) {
        return getSender(content).send(content);
    }

    @Override
    public void sendAsync(MessageContent content, MessageSendCallback callback) {
        getSender(content).sendAsync(content, callback);
    }

    @Override
    public MessageSendResult send(MessageContent content, MessagePriority priority) {
        // TODO 当前实现暂未根据 priority 区分发送策略，后续可扩展优先级队列或差异化发送逻辑
        Objects.requireNonNull(priority, "消息优先级不能为 null");
        return send(content);
    }

    /**
     * 根据消息类型获取对应发送器
     *
     * @param content 消息内容
     * @return 消息发送器
     */
    private MessageSender getSender(MessageContent content) {
        MessageSender sender = senderFactory.getSender(content.getMessageType());
        if (sender == null) {
            throw new IllegalStateException("未找到消息类型 " + content.getMessageType() + " 对应的发送器");
        }
        return sender;
    }
}
