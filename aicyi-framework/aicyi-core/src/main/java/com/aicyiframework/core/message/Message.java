package com.aicyiframework.core.message;

import com.aichuangyi.commons.lang.BaseBean;
import com.aichuangyi.commons.util.id.IdGenerator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 通用消息基类
 * @date 2025/8/25
 **/
public abstract class Message<T> extends BaseBean implements MessageContent<T> {
    private T content;// 消息内容
    private MessageType messageType; // 消息类型
    private String messageId; // 消息唯一ID
    private String businessId; // 业务ID
    private Map<String, Object> properties; // 扩展属性
    private LocalDateTime timestamp; // 发送时间

    public Message(T content, MessageType messageType) {
        this.content = content;
        this.messageType = messageType;
        this.messageId = IdGenerator.generateV7Id();
        this.properties = new HashMap<>();
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

