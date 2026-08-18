package io.github.aicyi.commons.core.message;

import io.github.aicyi.commons.lang.type.MessageType;

/**
 * @author Mr.Min
 * @description 消息内容接口
 * @date 2025/8/25
 **/
public interface MessageContent<T> {
    /**
     * 消息内容
     *
     * @return 消息体
     */
    T getContent();

    /**
     * 消息类型
     *
     * @return 消息类型枚举
     */
    MessageType getMessageType();

    /**
     * 消息ID
     *
     * @return 全局唯一消息标识
     */
    String getMessageId();
}