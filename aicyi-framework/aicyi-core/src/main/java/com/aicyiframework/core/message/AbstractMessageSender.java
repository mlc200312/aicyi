package com.aicyiframework.core.message;

import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aicyiframework.core.exception.MessageSendException;

import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 抽象消息发送器
 * @date 2025/8/25
 **/
public abstract class AbstractMessageSender implements MessageSender {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public SendResult send(MessageContent content) {
        try {
            validateContent(content);
            return doSend(content);
        } catch (MessageSendException e) {
            logger.error(e, "发送消息失败");
            return SendResult.builder().messageId(content.getMessageId()).buildFailure(e.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(MessageContent content, SendCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                SendResult result = send(content);
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
    protected abstract SendResult doSend(MessageContent content) throws MessageSendException;

    /**
     * 校验消息内容
     *
     * @param content
     */
    protected void validateContent(MessageContent content) {
        if (!supports(content.getMessageType())) {
            throw new UnsupportedOperationException("不支持的消息类型");
        }

        if (content == null || content.getContent() == null) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
    }
}