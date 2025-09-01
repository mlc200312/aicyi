package com.aicyiframework.message.mail;

import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aichuangyi.commons.core.exception.MessageSendException;
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
public class JavaMailEmailManager implements EmailManager {

    private static final Logger logger = LoggerFactory.getLogger(JavaMailEmailManager.class);

    private final EmailConfig emailConfig;
    private final Session session;
    private final TemplateEngine templateEngine; // 可选，用于模板渲染

    public JavaMailEmailManager(EmailConfig emailConfig) {
        this(emailConfig, new FreeMarkerTemplateEngine());
    }

    public JavaMailEmailManager(EmailConfig emailConfig, TemplateEngine templateEngine) {
        this.emailConfig = emailConfig;
        this.templateEngine = templateEngine;
        this.session = createSession();
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", emailConfig.getHost());
        props.put("mail.smtp.port", emailConfig.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", emailConfig.getConnectionTimeout());
        props.put("mail.smtp.timeout", emailConfig.getTimeout());

        if (emailConfig.isSslEnabled()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        if (emailConfig.isTlsEnabled()) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
            }
        });
    }

    @Override
    public boolean sendEmail(List<String> toList, List<String> ccList, String subject, String content, boolean isHtml, List<Attachment> attachments) throws MessageSendException {
        try {
            MimeMessageHelper messageHelper = createMimeMessageHelper(toList, ccList, subject);

            messageHelper.setText(content, isHtml);

            // 添加附件（如果有）
            if (attachments != null && !attachments.isEmpty()) {

                // 添加附件
                for (Attachment attachment : attachments) {

                    messageHelper.addAttachment(MimeUtility.encodeText(attachment.getName()), attachment.getFile());
                }
            }

            Transport.send(messageHelper.getMimeMessage());

            logger.info("邮件发送成功 - 收件人: {}, 主题: {}", toList, subject);

            return true;
        } catch (Exception e) {

            logger.error(e, "发送邮件失败 - 收件人: {}, 主题: {}", toList, subject);

            throw new MessageSendException("发送邮件失败:" + e.getMessage(), e);
        }
    }

    @Override
    public boolean sendTextEmail(List<String> toList, String subject, String content) {
        return sendEmail(toList, null, subject, content, false, null);
    }

    @Override
    public boolean sendHtmlEmail(List<String> toList, String subject, String htmlContent) {
        return sendEmail(toList, null, subject, htmlContent, true, null);
    }

    @Override
    public boolean sendEmailWithAttachment(List<String> toList, String subject, String content, List<Attachment> attachments) {
        return sendEmail(toList, null, subject, content, false, attachments);
    }

    @Override
    public boolean sendTemplateEmail(List<String> toList, String subject, String templateName, Map<String, Object> templateVariables) {
        if (templateEngine == null) {
            logger.error("未配置模板引擎，无法发送模板邮件");
            return false;
        }

        try {
            // 渲染模板
            String content = templateEngine.process(templateName, templateVariables);
            return sendHtmlEmail(toList, subject, content);
        } catch (Exception e) {
            logger.error(e, "发送模板邮件失败 - 收件人: {}, 模板: {}", toList, templateName);
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> sendEmailAsync(List<String> toList, String subject, String content) {
        return CompletableFuture.supplyAsync(() -> sendTextEmail(toList, subject, content));
    }

    @Override
    public boolean testConnection() {
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(emailConfig.getHost(), emailConfig.getPort(), emailConfig.getUsername(), emailConfig.getPassword());
            transport.close();
            return true;
        } catch (Exception e) {
            logger.error("邮件服务器连接测试失败", e);
            return false;
        }
    }

    private MimeMessageHelper createMimeMessageHelper(List<String> toList, List<String> ccList, String subject) throws MessagingException {
        MimeMessage message = new MimeMessage(session);

        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, emailConfig.getCharset());

        // 设置发件人
        if (emailConfig.getFromName() != null) {
            try {
                messageHelper.setFrom(emailConfig.getFromAddress(), emailConfig.getFromName());
            } catch (UnsupportedEncodingException e) {
                logger.error(e, "set from address error");
                throw new RuntimeException(e);
            }
        } else {
            messageHelper.setFrom(emailConfig.getFromAddress());
        }

        // 设置收件人
        messageHelper.setTo(toList.toArray(new String[toList.size()]));

        // 设置抄送人
        if (ccList != null && ccList.size() > 0) {
            messageHelper.setCc(ccList.toArray(new String[ccList.size()]));
        }

        // 设置主题
        messageHelper.setSubject(subject);

        // 设置发送时间
        messageHelper.setSentDate(new Date());

        return messageHelper;
    }
}