package com.aichuangyi.commons.core.message;

/**
 * @author Mr.Min
 * @description 消息内容接口
 * @date 2025/8/25
 **/
public interface MessageContent<T> {
    /**
     * 消息内容
     *
     * @return
     */
    T getContent();

    /**
     * 消息内容
     *
     * @return
     */
    MessageType getMessageType();

    /**
     * 消息ID
     *
     * @return
     */
    String getMessageId();
}