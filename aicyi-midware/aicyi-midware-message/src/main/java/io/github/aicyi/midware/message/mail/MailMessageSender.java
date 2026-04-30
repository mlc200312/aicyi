package io.github.aicyi.midware.message.mail;

import io.github.aicyi.commons.core.message.AbstractMessageSender;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.core.message.MessageType;
import io.github.aicyi.commons.core.message.SendResult;
import io.github.aicyi.commons.core.message.MessageSendException;

/**
 * @author Mr.Min
 * @description Email发送器实现
 * @date 2025/8/25
 **/
public class MailMessageSender extends AbstractMessageSender {
    private final MailManager mailManager;

    public MailMessageSender(MailManager mailManager) {
        this.mailManager = mailManager;
    }

    public MailManager getEmailManager() {
        return mailManager;
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.EMAIL.equals(messageType);
    }

    @Override
    protected SendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof MailMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        MailMessage message = (MailMessage) content;
        // 调用实际的邮件发送服务

        mailManager.sendEmail(message.getToList(), message.getCcList(), message.getSubject(), message.getContent(), message.isHtml(), message.getAttachments());

        return SendResult.success(message.getMessageId(), message.getBusinessId());
    }
}