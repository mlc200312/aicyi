package com.aicyiframework.core.message;

/**
 * MQ消息服务
 */
public interface MqService {
    String send(
            String topic,
            String tag,
            String body,
            String key,
            Integer delayLevel) throws MessageSendException;
}
