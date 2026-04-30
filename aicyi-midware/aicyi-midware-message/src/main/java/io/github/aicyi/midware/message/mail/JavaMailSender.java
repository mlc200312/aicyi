package io.github.aicyi.midware.message.mail;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.message.MessageSendException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 基于JavaMail的EmailManager实现
 * @date 2025/8/25
 **/
public class JavaMailSender implements MailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMailSender.class);

    private final MailConfig mailConfig;
    private final Session session;
    private final TemplateEngine templateEngine; // 可选，用于模板渲染

    public JavaMailSender(MailConfig mailConfig) {
        this(mailConfig, new FreeMarkerTemplateEngine());
    }

    public JavaMailSender(MailConfig mailConfig, TemplateEngine templateEngine) {
        this.mailConfig = mailConfig;
        this.templateEngine = templateEngine;
        this.session = createSession();
    }

    @Override
    public boolean send(List<String> toRecipients, List<String> ccRecipients, String subject, String body, boolean htmlFormat, List<MailAttachment> attachments) throws MessageSendException {
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

            LOGGER.info("邮件发送成功 - 收件人: {}, 主题: {}", toRecipients, subject);

            return true;
        } catch (Exception e) {

            LOGGER.error(e, "发送邮件失败 - 收件人: {}, 主题: {}", toRecipients, subject);

            throw new MessageSendException("发送邮件失败:" + e.getMessage(), e);
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
    public boolean sendTemplate(List<String> toRecipients, String subject, String templateName, Map<String, Object> templateVariables) {
        if (templateEngine == null) {
            LOGGER.error("未配置模板引擎，无法发送模板邮件");
            return false;
        }

        try {
            // 渲染模板
            String body = templateEngine.process(templateName, templateVariables);
            return sendHtml(toRecipients, subject, body);
        } catch (Exception e) {
            LOGGER.error(e, "发送模板邮件失败 - 收件人: {}, 模板: {}", toRecipients, templateName);
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> sendAsync(List<String> toRecipients, String subject, String body) {
        return CompletableFuture.supplyAsync(() -> sendText(toRecipients, subject, body));
    }

    @Override
    public boolean testConnection() {
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(mailConfig.getHost(), mailConfig.getPort(), mailConfig.getUsername(), mailConfig.getPassword());
            transport.close();
            return true;
        } catch (Exception e) {
            LOGGER.error("邮件服务器连接测试失败", e);
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
                LOGGER.error(e, "set from address error");
                throw new RuntimeException(e);
            }
        } else {
            messageHelper.setFrom(mailConfig.getFromAddress());
        }

        // 设置收件人
        messageHelper.setTo(toRecipients.toArray(new String[toRecipients.size()]));

        // 设置抄送人
        if (CollectionUtils.isNotEmpty(ccRecipients)) {
            messageHelper.setCc(ccRecipients.toArray(new String[ccRecipients.size()]));
        }

        // 设置主题
        messageHelper.setSubject(subject);

        // 设置发送时间
        messageHelper.setSentDate(new Date());

        return messageHelper;
    }
}