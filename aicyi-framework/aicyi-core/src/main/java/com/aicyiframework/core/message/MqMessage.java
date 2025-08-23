package com.aicyiframework.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQ消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqMessage extends Message {
    private String topic; // 消息主题
    private String tag; // 消息标签
    private String body; // 消息体
    private String key; // 业务key
    private Integer delayLevel; // 延迟级别
}
