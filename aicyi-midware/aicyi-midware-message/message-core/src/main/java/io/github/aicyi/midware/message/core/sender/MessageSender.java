package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.model.MessageType;
import io.github.aicyi.midware.message.core.model.MessageSendResult;

/**
 * @author Mr.Min
 * @description 消息发送服务接口
 * @date 2025/8/25
 **/
public interface MessageSender {
    /**
     * 发送消息
     *
     * @param content 消息对象
     * @return 发送结果
     */
    MessageSendResult send(MessageContent content);

    /**
     * 异步发送消息
     *
     * @param content  消息内容
     * @param callback 发送结果回调
     */
    void sendAsync(MessageContent content, MessageSendCallback callback);

    /**
     * 是否支持该消息类型
     *
     * @param messageType 消息类型
     * @return 是否支持
     */
    boolean supports(MessageType messageType);
}