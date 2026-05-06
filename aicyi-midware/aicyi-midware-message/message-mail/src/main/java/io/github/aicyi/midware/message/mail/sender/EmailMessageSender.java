package io.github.aicyi.midware.message.mail.sender;


import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.model.MessageType;
import io.github.aicyi.midware.message.core.model.MessageSendResult;
import io.github.aicyi.midware.message.core.sender.AbstractMessageSender;
import io.github.aicyi.midware.message.mail.model.MailMessage;

/**
 * @author Mr.Min
 * @description Email发送器实现
 * @date 2025/8/25
 **/
public class EmailMessageSender extends AbstractMessageSender {
    private final EmailSender EMailSender;

    public EmailMessageSender(EmailSender EMailSender) {
        this.EMailSender = EMailSender;
    }


    @Override
    protected void validate(MessageContent content) {
        if (!supports(content.getMessageType())) {
            throw new UnsupportedOperationException("不支持的消息类型");
        }

        if (!(content instanceof MailMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        MailMessage message = (MailMessage) content;

        if (!message.isValid()) {
            throw new IllegalArgumentException("消息参数错误");
        }
    }

    @Override
    protected MessageSendResult doSend(MessageContent content) throws MessageSendException {
        if (!(content instanceof MailMessage)) {
            throw new IllegalArgumentException("不支持的消息类型");
        }

        MailMessage message = (MailMessage) content;
        // 调用实际的邮件发送服务

        EMailSender.send(message.getToList(), message.getCcList(), message.getSubject(), message.getContent(), message.isHtml(), message.getAttachments());

        return MessageSendResult.success(message.getMessageId(), message.getBusinessId());
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.MAIL.equals(messageType);
    }
}