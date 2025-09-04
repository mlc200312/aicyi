package io.github.aicyi.commons.core.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Min
 * @description 消息发送器工厂实现
 * @date 2025/8/25
 **/
public class DefaultMessageSenderFactory implements MessageSenderFactory {
    private Map<MessageType, MessageSender> senderMap = new ConcurrentHashMap<>();

    @Override
    public MessageSender getSender(MessageType messageType) {
        MessageSender sender = senderMap.get(messageType);
        if (sender == null) {
            throw new IllegalArgumentException("未找到对应的消息发送器: " + messageType);
        }
        return sender;
    }

    @Override
    public void registerSender(MessageType messageType, MessageSender sender) {
        if (sender.supports(messageType)) {
            senderMap.put(messageType, sender);
        }
    }
}
