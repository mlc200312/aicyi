package com.aicyiframework.core.message;

import java.util.List;

/**
 * 邮件发送服务
 */
public interface EmailService {
    String send(
            List<String> to,
            String subject,
            String content,
            boolean isHtml,
            List<Attachment> attachments) throws MessageSendException;
}

