package com.aicyiframework.message.mail;

import com.aicyiframework.core.message.AbstractMessageSender;
import com.aicyiframework.core.message.MessageContent;
import com.aicyiframework.core.message.MessageType;
import com.aicyiframework.core.message.SendResult;
import com.aicyiframework.core.exception.MessageSendException;

/**
 * @author Mr.Min
 * @description Email发送器实现
 * @date 2025/8/25
 **/
public class EmailMessageSender extends AbstractMessageSender {
    private final EmailManager emailManager;

    public EmailMessageSender(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    public EmailManager getEmailManager() {
        return emailManager;
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.EMAIL.equals(messageType);
    }

    @Override
    protected SendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof EmailMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        EmailMessage message = (EmailMessage) content;
        // 调用实际的邮件发送服务

        emailManager.sendEmail(message.getToList(), message.getCcList(), message.getSubject(), message.getContent(), message.isHtml(), message.getAttachments());

        return SendResult.success(message.getMessageId(), message.getBusinessId());
    }
}