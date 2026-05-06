package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.midware.message.core.model.MessageSendResult;

import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 抽象消息发送器
 * @date 2025/8/25
 **/
public abstract class AbstractMessageSender implements MessageSender {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public MessageSendResult send(MessageContent content) {
        try {
            validate(content);
            return doSend(content);
        } catch (MessageSendException e) {
            LOGGER.error(e, "发送消息失败");
            return MessageSendResult.builder()
                    .messageId(content.getMessageId())
                    .buildFailure(e.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(MessageContent content, MessageSendCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                MessageSendResult result = send(content);
                callback.onComplete(result);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 发送
     *
     * @param content
     * @return
     * @throws MessageSendException
     */
    protected abstract MessageSendResult doSend(MessageContent content) throws MessageSendException;

    /**
     * 校验消息内容
     *
     * @param content
     */
    protected void validate(MessageContent content) {
        if (!supports(content.getMessageType())) {
            throw new UnsupportedOperationException("不支持的消息类型");
        }

        if (content == null || content.getContent() == null) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
    }
}