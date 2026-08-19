package io.github.aicyi.midware.message.sms.sender.impl;

import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.midware.message.core.template.AbstractTemplateSender;
import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.commons.core.template.TemplateEngine;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.sms.model.SmsMessage;
import io.github.aicyi.midware.message.sms.sender.SmsSender;
import org.slf4j.MDC;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr.Min
 * @description 抽象的短信发送者实现
 * @date 09:55
 **/
public abstract class AbstractSmsSender extends AbstractTemplateSender<SmsMessage> implements SmsSender {

    private static final int DEFAULT_CORE_POOL_SIZE = 2;
    private static final int DEFAULT_MAX_POOL_SIZE = 5;
    private static final long DEFAULT_KEEP_ALIVE_SECONDS = 60L;
    private static final int DEFAULT_QUEUE_CAPACITY = 500;

    protected final ExecutorService executorService;

    /**
     * 是否持有默认线程池的生命周期（外部注入的执行器由调用方负责关闭）
     */
    private final boolean ownedExecutor;

    public AbstractSmsSender(TemplateProvider templateProvider, TemplateEngineFactory factory, ExecutorService executorService) {
        super(templateProvider, factory);
        this.executorService = executorService;
        this.ownedExecutor = false;
    }

    public AbstractSmsSender(TemplateProvider templateProvider, TemplateEngineFactory factory) {
        super(templateProvider, factory);
        // 阿里手册【强制】：线程池必须通过 ThreadPoolExecutor 创建，禁止 Executors 快捷方式（无界队列 OOM 风险）
        this.executorService = new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SIZE,
                DEFAULT_MAX_POOL_SIZE,
                DEFAULT_KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY),
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "sms-sender-async-" + counter.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.ownedExecutor = true;
    }

    @Override
    public CompletableFuture<Boolean> sendAsync(List<String> phoneNumbers, String messageContent, String sign) {
        // 捕获提交方 MDC 上下文，保证异步线程内 traceId 等链路日志不丢失
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return CompletableFuture.supplyAsync(() -> {
            if (mdcContext != null) {
                MDC.setContextMap(mdcContext);
            }
            try {
                phoneNumbers.forEach(number -> send(number, messageContent, sign));
                return true;
            } finally {
                // 线程池复用线程，必须清理 MDC 防止上下文污染
                MDC.clear();
            }
        }, executorService);
    }


    @Override
    protected boolean doSend(MessageTemplate template, SmsMessage message) {
        try {
            TemplateEngine templateEngine = getTemplateEngine(TemplateEngineType.valueOf(template.getEngineType()));

            String content = templateEngine.process(template.getContent(), message.getTemplateParams());

            message.getPhoneNumbers().forEach(phoneNumber -> send(phoneNumber, content, template.getSignature()));

            return true;
        } catch (MessageSendException e) {

            logger.error(e, "发送模板短信失败 - 手机号: {}, 模板: {}", message.getPhoneNumbers(), template.getTemplateCode());

            return false;
        }
    }

    /**
     * 关闭线程池资源（仅关闭默认自持线程池，外部注入的执行器由调用方管理生命周期）
     */
    @PreDestroy
    public void shutdown() {
        if (ownedExecutor) {
            executorService.shutdown();
        }
    }
}
