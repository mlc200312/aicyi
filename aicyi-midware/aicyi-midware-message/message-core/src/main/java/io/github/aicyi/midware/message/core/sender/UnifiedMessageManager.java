package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.model.MessagePriority;
import io.github.aicyi.midware.message.core.model.MessageSendResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description 统一消息发送服务
 * @date 2025/8/25
 **/
public interface UnifiedMessageManager {

    MessageSendResult send(MessageContent content);

    void sendAsync(MessageContent content, MessageSendCallback callback);

    MessageSendResult send(MessageContent content, MessagePriority priority);

    default List<MessageSendResult> sendBatch(List<MessageContent> contents) {
        return contents.stream().map(this::send).collect(Collectors.toList());
    }
}