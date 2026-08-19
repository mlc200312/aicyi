package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.message.MessageSendCallback;
import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.message.MessageSendResult;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.midware.message.core.exception.MessageResultCode;
import io.github.aicyi.midware.message.core.exception.MessageSendException;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr.Min
 * @description 抽象消息发送器
 * @date 2025/8/25
 **/
public abstract class AbstractMessageSender implements MessageSender {

    private static final int DEFAULT_CORE_POOL_SIZE = 2;
    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private static final long DEFAULT_KEEP_ALIVE_SECONDS = 60L;
    private static final int DEFAULT_QUEUE_CAPACITY = 1000;

    /**
     * 默认异步发送线程池：显式 ThreadPoolExecutor（有界队列 + 命名线程 + CallerRunsPolicy），
     * 避免使用 ForkJoinPool.commonPool 导致任务互相阻塞与 MDC 上下文丢失
     */
    private static final Executor DEFAULT_ASYNC_EXECUTOR = new ThreadPoolExecutor(
            DEFAULT_CORE_POOL_SIZE,
            DEFAULT_MAX_POOL_SIZE,
            DEFAULT_KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "message-sender-async-" + counter.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 异步发送执行器，允许业务侧注入自定义线程池覆盖默认实现
     */
    private volatile Executor asyncExecutor = DEFAULT_ASYNC_EXECUTOR;

    public void setAsyncExecutor(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public MessageSendResult send(MessageContent<?> content) {
        try {
            validate(content);
            return doSend(content);
        } catch (MessageSendException e) {
            logger.error(e, "发送消息失败");
            return MessageSendResult.failure(content.getMessageId(), e.getCodeAsString(), e.getMessage());
        } catch (Exception e) {
            // 与异步路径行为一致：参数校验等非发送异常同样收敛为失败结果，不向调用方外抛
            logger.error(e, "发送消息失败");
            return MessageSendResult.failure(content.getMessageId(),
                    String.valueOf(MessageResultCode.MESSAGE_SEND_ERROR.getCode()), e.getMessage());
        }
    }

    @Override
    public void sendAsync(MessageContent<?> content, MessageSendCallback callback) {
        Assert.notNull(callback, "callback");
        // 捕获提交方 MDC 上下文，保证异步线程内 traceId 等链路日志不丢失
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        CompletableFuture.runAsync(() -> {
            if (mdcContext != null) {
                MDC.setContextMap(mdcContext);
            }
            try {
                MessageSendResult result = send(content);
                callback.onComplete(result);
            } catch (Exception e) {
                notifyError(callback, e);
            } finally {
                // 线程池复用线程，必须清理 MDC 防止上下文污染
                MDC.clear();
            }
        }, asyncExecutor);
    }

    /**
     * 发送
     *
     * @param content 消息内容
     * @return 发送结果
     * @throws MessageSendException 发送异常
     */
    protected abstract MessageSendResult doSend(MessageContent<?> content) throws MessageSendException;

    /**
     * 校验消息内容
     *
     * @param content 消息内容
     */
    protected void validate(MessageContent<?> content) {
        if (!supports(content.getMessageType())) {
            throw new MessageSendException(MessageResultCode.MESSAGE_NOT_SUPPORTED,
                    "不支持的消息类型: " + content.getMessageType());
        }

        if (content.getContent() == null) {
            throw new MessageSendException(MessageResultCode.MESSAGE_PARAM_ERROR, "消息内容不能为空");
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
