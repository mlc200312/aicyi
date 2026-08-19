package io.github.aicyi.midware.message.mail.sender.impl;

import io.github.aicyi.midware.message.core.exception.MessageResultCode;
import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.midware.message.core.model.MessageFormat;
import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.midware.message.core.template.AbstractTemplateSender;
import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.mail.model.MailAttachment;
import io.github.aicyi.midware.message.mail.config.MailConfig;
import io.github.aicyi.midware.message.mail.model.MailMessage;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.commons.core.template.TemplateEngine;
import org.slf4j.MDC;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr.Min
 * @description 基于 JavaMail 的邮件发送服务实现
 * @date 2025/8/25
 **/
public class JavaMailEmailSender extends AbstractTemplateSender<MailMessage> implements EmailSender {

    private static final int DEFAULT_CORE_POOL_SIZE = 2;
    private static final int DEFAULT_MAX_POOL_SIZE = 5;
    private static final long DEFAULT_KEEP_ALIVE_SECONDS = 60L;
    private static final int DEFAULT_QUEUE_CAPACITY = 500;

    /**
     * 异步发送线程池：显式 ThreadPoolExecutor（有界队列 + 命名线程），禁止使用 ForkJoinPool.commonPool
     */
    private static final ExecutorService ASYNC_EXECUTOR = new ThreadPoolExecutor(
            DEFAULT_CORE_POOL_SIZE,
            DEFAULT_MAX_POOL_SIZE,
            DEFAULT_KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "mail-sender-async-" + counter.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private final MailConfig mailConfig;
    private final Session session;

    public JavaMailEmailSender(TemplateProvider templateProvider, TemplateEngineFactory factory, MailConfig mailConfig) {
        super(templateProvider, factory);
        this.mailConfig = mailConfig;
        this.session = createSession();
    }

    public JavaMailEmailSender(MailConfig mailConfig) {
        this(templateCode -> null, null, mailConfig);
    }

    @Override
    public boolean send(List<String> toRecipients,
                        List<String> ccRecipients,
                        String subject,
                        String body,
                        boolean htmlFormat,
                        List<MailAttachment> attachments) throws MessageSendException {
        try {
            MimeMessageHelper messageHelper = createMimeMessageHelper(toRecipients, ccRecipients, subject);

            messageHelper.setText(body, htmlFormat);

            // 添加附件（如果有）
            if (attachments != null && !attachments.isEmpty()) {

                // 添加附件
                for (MailAttachment attachment : attachments) {

                    messageHelper.addAttachment(MimeUtility.encodeText(attachment.getName()), attachment.getFile());
                }
            }

            Transport.send(messageHelper.getMimeMessage());

            logger.info("邮件发送成功 - 收件人: {}, 主题: {}", toRecipients, subject);

            return true;
        } catch (Exception e) {

            logger.error(e, "发送邮件失败 - 收件人: {}, 主题: {}", toRecipients, subject);

            throw new MessageSendException(MessageResultCode.MESSAGE_SEND_ERROR, "发送邮件失败:" + e.getMessage(), e);
        }
    }

    @Override
    public boolean sendText(List<String> toRecipients, String subject, String body) {
        return send(toRecipients, null, subject, body, false, null);
    }

    @Override
    public boolean sendHtml(List<String> toRecipients, String subject, String html) {
        return send(toRecipients, null, subject, html, true, null);
    }

    @Override
    public boolean sendWithAttachment(List<String> toRecipients, String subject, String body, List<MailAttachment> attachments) {
        return send(toRecipients, null, subject, body, false, attachments);
    }

    @Override
    public CompletableFuture<Boolean> sendAsync(List<String> toRecipients, String subject, String body) {
        // 捕获提交方 MDC 上下文，保证异步线程内 traceId 等链路日志不丢失
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return CompletableFuture.supplyAsync(() -> {
            if (mdcContext != null) {
                MDC.setContextMap(mdcContext);
            }
            try {
                return sendText(toRecipients, subject, body);
            } finally {
                // 线程池复用线程，必须清理 MDC 防止上下文污染
                MDC.clear();
            }
        }, ASYNC_EXECUTOR);
    }

    @Override
    public boolean testConnection() {
        // try-with-resources 保证异常路径下 Transport 连接也能释放
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect(mailConfig.getHost(), mailConfig.getPort(), mailConfig.getUsername(), mailConfig.getPassword());
            return true;
        } catch (Exception e) {
            logger.error("邮件服务器连接测试失败", e);
            return false;
        }
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailConfig.getHost());
        props.put("mail.smtp.port", mailConfig.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", mailConfig.getConnectionTimeout());
        props.put("mail.smtp.timeout", mailConfig.getTimeout());

        if (mailConfig.isSslEnabled()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        if (mailConfig.isTlsEnabled()) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConfig.getUsername(), mailConfig.getPassword());
            }
        });
    }

    private MimeMessageHelper createMimeMessageHelper(List<String> toRecipients, List<String> ccRecipients, String subject) throws MessagingException {
        MimeMessage message = new MimeMessage(session);

        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, mailConfig.getCharset());

        // 设置发件人
        if (mailConfig.getFromName() != null) {
            try {

                messageHelper.setFrom(mailConfig.getFromAddress(), mailConfig.getFromName());

            } catch (UnsupportedEncodingException e) {

                logger.error(e, "set from address error");

                throw new MessageSendException(MessageResultCode.MESSAGE_SEND_ERROR, "发件人名称编码失败", e);
            }
        } else {
            messageHelper.setFrom(mailConfig.getFromAddress());
        }

        // 设置收件人
        messageHelper.setTo(toRecipients.toArray(new String[0]));

        // 设置抄送人
        if (ccRecipients != null && !ccRecipients.isEmpty()) {

            messageHelper.setCc(ccRecipients.toArray(new String[0]));
        }

        // 设置主题
        messageHelper.setSubject(subject);

        // 设置发送时间
        messageHelper.setSentDate(new Date());

        return messageHelper;
    }

    @Override
    protected boolean doSend(MessageTemplate template, MailMessage message) {

        TemplateEngine templateEngine = getTemplateEngine(TemplateEngineType.valueOf(template.getEngineType()));

        try {
            // 渲染模板
            String subject = templateEngine.process(template.getSubject(), message.getTemplateParams());

            String content = templateEngine.process(template.getContent(), message.getTemplateParams());

            if (MessageFormat.HTML.name().equalsIgnoreCase(template.getFormat())) {

                return sendHtml(message.getToList(), subject, content);
            }

            return send(message.getToList(), message.getCcList(), subject, content, false, message.getAttachments());
        } catch (MessageSendException e) {

            logger.error(e, "发送模板邮件失败 - 收件人: {}, 模板: {}", message.getToList(), template);

            return false;
        }
    }
}
