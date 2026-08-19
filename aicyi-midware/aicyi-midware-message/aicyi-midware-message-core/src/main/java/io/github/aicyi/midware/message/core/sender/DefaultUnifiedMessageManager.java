package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.core.message.MessageSendCallback;
import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.core.message.MessageSendResult;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.midware.message.core.model.MessagePriority;

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
        Assert.notNull(senderFactory, "senderFactory");
        this.senderFactory = senderFactory;
    }

    @Override
    public MessageSendResult send(MessageContent<?> content) {
        return getSender(content).send(content);
    }

    @Override
    public void sendAsync(MessageContent<?> content, MessageSendCallback callback) {
        getSender(content).sendAsync(content, callback);
    }

    /**
     * 按优先级发送消息
     * <p>
     * 注意：当前版本实现暂未根据 priority 区分发送策略，实际行为等价于 {@link #send(MessageContent)}，
     * 保留该参数以便后续扩展优先级队列或差异化发送逻辑
     *
     * @param content  消息内容
     * @param priority 消息优先级（当前版本仅校验非空，不影响发送行为）
     * @return 发送结果
     */
    @Override
    public MessageSendResult send(MessageContent<?> content, MessagePriority priority) {
        Assert.notNull(priority, "消息优先级");
        return send(content);
    }

    /**
     * 根据消息类型获取对应发送器
     *
     * @param content 消息内容
     * @return 消息发送器
     */
    private MessageSender getSender(MessageContent<?> content) {
        MessageSender sender = senderFactory.getSender(content.getMessageType());
        if (sender == null) {
            throw new IllegalStateException("未找到消息类型 " + content.getMessageType() + " 对应的发送器");
        }
        return sender;
    }
}
