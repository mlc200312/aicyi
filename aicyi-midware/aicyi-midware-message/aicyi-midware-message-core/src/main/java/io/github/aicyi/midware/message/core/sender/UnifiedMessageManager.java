package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.core.message.MessageSendCallback;
import io.github.aicyi.commons.lang.model.MessageSendResult;
import io.github.aicyi.midware.message.core.model.MessagePriority;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description 统一消息发送服务
 * @date 2025/8/25
 **/
public interface UnifiedMessageManager {

    /**
     * 发送消息
     *
     * @param content 消息内容，不能为 null
     * @return 发送结果
     */
    MessageSendResult send(MessageContent content);

    /**
     * 异步发送消息
     *
     * @param content  消息内容，不能为 null
     * @param callback 发送结果回调，不能为 null
     */
    void sendAsync(MessageContent content, MessageSendCallback callback);

    /**
     * 按优先级发送消息
     *
     * @param content  消息内容，不能为 null
     * @param priority 消息优先级，不能为 null
     * @return 发送结果
     */
    MessageSendResult send(MessageContent content, MessagePriority priority);

    /**
     * 批量发送消息
     *
     * @param contents 消息内容列表，不能为 null
     * @return 发送结果列表
     */
    default List<MessageSendResult> sendBatch(List<MessageContent> contents) {
        Objects.requireNonNull(contents, "消息内容列表不能为 null");
        return contents.stream()
                .map(this::send)
                .collect(Collectors.toList());
    }
}