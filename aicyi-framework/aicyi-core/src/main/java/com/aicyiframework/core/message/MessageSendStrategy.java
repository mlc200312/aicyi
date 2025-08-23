package com.aicyiframework.core.message;

/**
 * 消息发送策略接口
 */
public interface MessageSendStrategy {
    /**
     * 是否支持此消息类型
     */
    boolean supports(MessageType messageType);
    
    /**
     * 发送消息
     */
    SendResult send(Message message);
}