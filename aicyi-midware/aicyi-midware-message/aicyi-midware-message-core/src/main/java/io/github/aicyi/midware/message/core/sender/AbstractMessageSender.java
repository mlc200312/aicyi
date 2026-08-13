package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.message.MessageSendCallback;
import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.lang.model.MessageSendResult;
import io.github.aicyi.commons.util.Assert;
import io.github.aicyi.midware.message.core.exception.MessageSendException;

import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 抽象消息发送器
 * @date 2025/8/25
 **/
public abstract class AbstractMessageSender implements MessageSender {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public MessageSendResult send(MessageContent content) {
        try {
            validate(content);
            return doSend(content);
        } catch (MessageSendException e) {
            logger.error(e, "发送消息失败");
            return MessageSendResult.builder()
                    .messageId(content.getMessageId())
                    .buildFailure(e.getCode(), e.getMessage());
        }
    }

    @Override
    public void sendAsync(MessageContent content, MessageSendCallback callback) {
        Assert.notNull(callback, "callback");
        CompletableFuture.runAsync(() -> {
            try {
                MessageSendResult result = send(content);
                callback.onComplete(result);
            } catch (Exception e) {
                notifyError(callback, e);
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

        if (content.getContent() == null) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
    }

    /**
     * 安全地通知回调发生异常，避免回调本身抛异常导致异步任务异常终止
     *
     * @param callback 回调对象
     * @param e        异常
     */
    private void notifyError(MessageSendCallback callback, Exception e) {
        try {
            callback.onError(e);
        } catch (Exception ex) {
            logger.error(ex, "消息发送回调 onError 处理失败");
        }
    }
}