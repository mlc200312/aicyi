package io.github.aicyi.midware.message.mail.sender;

import io.github.aicyi.midware.message.mail.model.MailAttachment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 邮件发送服务
 * @date 2025/8/25
 **/
public interface EmailSender {
    /**
     * 发送邮件（完整参数）
     *
     * @param toRecipients
     * @param ccRecipients
     * @param subject
     * @param body
     * @param htmlFormat
     * @param attachments
     * @return
     */
    boolean send(
            List<String> toRecipients,
            List<String> ccRecipients,
            String subject,
            String body,
            boolean htmlFormat,
            List<MailAttachment> attachments);

    /**
     * 发送简单文本邮件
     *
     * @param toRecipients
     * @param subject
     * @param body
     * @return
     */
    boolean sendText(List<String> toRecipients, String subject, String body);

    /**
     * 发送HTML格式邮件
     *
     * @param toRecipients
     * @param subject
     * @param html
     * @return
     */
    boolean sendHtml(List<String> toRecipients, String subject, String html);

    /**
     * 发送带附件的邮件
     *
     * @param toRecipients
     * @param subject
     * @param body
     * @param attachments
     * @return
     */
    boolean sendWithAttachment(List<String> toRecipients, String subject, String body, List<MailAttachment> attachments);

    /**
     * 发送模板邮件
     *
     * @param toRecipients
     * @param subject
     * @param templateId
     * @param templateParams
     * @return
     */
    boolean sendTemplate(List<String> toRecipients, String subject, String templateId, Map<String, Object> templateParams);

    /**
     * 异步发送邮件
     *
     * @param toRecipients
     * @param subject
     * @param body
     * @return
     */
    CompletableFuture<Boolean> sendAsync(List<String> toRecipients, String subject, String body);

    /**
     * 测试邮件服务器连接
     *
     * @return
     */
    boolean testConnection();
}