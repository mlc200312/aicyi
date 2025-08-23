package com.aicyiframework.core.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用消息发送器实现
 */
@Slf4j
public class UniversalMessageSender implements MessageSender, InitializingBean {
    
    private final Map<MessageType, MessageSendStrategy> strategyMap = new ConcurrentHashMap<>();
    private final List<MessageSendStrategy> strategies;
    
    public UniversalMessageSender(List<MessageSendStrategy> strategies) {
        this.strategies = strategies;
    }
    
    @Override
    public void afterPropertiesSet() {
        // 初始化时自动注册所有策略
        strategies.forEach(strategy -> {
            Arrays.stream(MessageType.values())
                .filter(strategy::supports)
                .forEach(type -> registerStrategy(type, strategy));
        });
    }
    
    @Override
    public SendResult send(Message message) {
        if (message == null) {
            return SendResult.failure("NULL_MESSAGE", "Message cannot be null");
        }
        
        MessageSendStrategy strategy = strategyMap.get(message.getMessageType());
        if (strategy == null) {
            return SendResult.failure("UNSUPPORTED_TYPE", 
                "No strategy found for message type: " + message.getMessageType());
        }
        
        try {
            return strategy.send(message);
        } catch (Exception e) {
            log.error("Send message failed, messageId: {}", message.getMessageId(), e);
            return SendResult.failure("SEND_ERROR", e.getMessage());
        }
    }
    
    @Override
    public void registerStrategy(MessageType messageType, MessageSendStrategy strategy) {
        strategyMap.put(messageType, strategy);
        log.info("Registered message strategy {} for type {}", 
            strategy.getClass().getSimpleName(), messageType);
    }
}