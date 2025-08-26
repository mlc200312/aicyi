package com.aicyiframework.core.mq;

import com.aicyiframework.core.exception.MessageSendException;

/**
 * @author Mr.Min
 * @description MQ消息服务
 * @date 2025/8/25
 **/
public interface MqManager {
    String send(
            String topic,
            String tag,
            Object body,
            String key,
            Integer delayLevel) throws MessageSendException;
}
