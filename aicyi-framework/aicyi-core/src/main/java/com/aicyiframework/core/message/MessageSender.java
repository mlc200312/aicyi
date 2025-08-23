package com.aicyiframework.core.message;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息发送服务接口
 */
public interface MessageSender {
    /**
     * 发送消息
     * @param message 消息对象
     * @return 发送结果
     */
    SendResult send(Message message);
    
    /**
     * 批量发送消息
     * @param messages 消息列表
     * @return 发送结果列表
     */
    default List<SendResult> sendBatch(List<Message> messages) {
        return messages.stream().map(this::send).collect(Collectors.toList());
    }
    
    /**
     * 注册新的消息类型处理器
     * @param messageType 消息类型
     * @param strategy 发送策略
     */
    void registerStrategy(MessageType messageType, MessageSendStrategy strategy);
}