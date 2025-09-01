package com.aichuangyi.commons.core.message;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description 统一消息发送服务
 * @date 2025/8/25
 **/
public interface UnifiedMessageManager {

    SendResult send(MessageContent content);

    void sendAsync(MessageContent content, SendCallback callback);

    SendResult send(MessageContent content, MessagePriority priority);

    default List<SendResult> sendBatch(List<MessageContent> contents) {
        return contents.stream().map(this::send).collect(Collectors.toList());
    }
}