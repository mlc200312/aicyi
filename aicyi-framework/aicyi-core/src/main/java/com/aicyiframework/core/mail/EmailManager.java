package com.aicyiframework.core.mail;

import com.aicyiframework.core.exception.MessageSendException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 邮件发送服务
 * @date 2025/8/25
 **/
public interface EmailManager {
    /**
     * 发送邮件（完整参数）
     *
     * @param toList
     * @param ccList
     * @param subject
     * @param content
     * @param isHtml
     * @param attachments
     * @return
     * @throws MessageSendException
     */
    boolean sendEmail(
            List<String> toList,
            List<String> ccList,
            String subject,
            String content,
            boolean isHtml,
            List<Attachment> attachments);

    /**
     * 发送简单文本邮件
     *
     * @param toList
     * @param subject
     * @param content
     * @return
     * @throws MessageSendException
     */
    boolean sendTextEmail(List<String> toList, String subject, String content);

    /**
     * 发送HTML格式邮件
     *
     * @param toList
     * @param subject
     * @param htmlContent
     * @return
     * @throws MessageSendException
     */
    boolean sendHtmlEmail(List<String> toList, String subject, String htmlContent);

    /**
     * 发送带附件的邮件
     *
     * @param toList
     * @param subject
     * @param content
     * @param attachments
     * @return
     * @throws MessageSendException
     */
    boolean sendEmailWithAttachment(List<String> toList, String subject, String content, List<Attachment> attachments);

    /**
     * 发送模板邮件
     *
     * @param toList
     * @param subject
     * @param templateName
     * @param templateVariables
     * @return
     * @throws MessageSendException
     */
    boolean sendTemplateEmail(List<String> toList, String subject, String templateName, Map<String, Object> templateVariables);

    /**
     * 异步发送邮件
     *
     * @param toList
     * @param subject
     * @param content
     * @return
     * @throws MessageSendException
     */
    CompletableFuture<Boolean> sendEmailAsync(List<String> toList, String subject, String content);

    /**
     * 测试邮件服务器连接
     *
     * @return
     */
    boolean testConnection();
}