package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.midware.message.core.model.MessageType;

/**
 * @author Mr.Min
 * @description 消息发送器工厂
 * @date 2025/8/25
 **/
public interface MessageSenderFactory {

    MessageSender getSender(MessageType messageType);

    void registerSender(MessageType messageType, MessageSender sender);
}