package com.aicyiframework.core.message;

import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 通用消息基类
 */
@Data
public abstract class Message {
    private String messageId = UUID.randomUUID().toString(); // 消息唯一ID
    private MessageType messageType; // 消息类型
    private String businessId; // 业务ID
    private Map<String, Object> properties; // 扩展属性
    private Date sendTime = new Date(); // 发送时间
}

