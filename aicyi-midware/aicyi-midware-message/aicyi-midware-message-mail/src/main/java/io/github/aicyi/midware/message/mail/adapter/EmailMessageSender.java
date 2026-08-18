package io.github.aicyi.midware.message.mail.adapter;


import io.github.aicyi.midware.message.core.exception.MessageSendException;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.lang.type.MessageType;
import io.github.aicyi.commons.lang.model.MessageSendResult;
import io.github.aicyi.midware.message.core.sender.AbstractMessageSender;
import io.github.aicyi.midware.message.mail.model.MailMessage;
import io.github.aicyi.midware.message.mail.sender.EmailSender;

/**
 * @author Mr.Min
 * @description Email消息发送器适配器，对接通用消息框架
 * @date 2025/8/25
 **/
public class EmailMessageSender extends AbstractMessageSender {
    private final EmailSender emailSender;

    public EmailMessageSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    protected void validate(MessageContent<?> content) {
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
    protected MessageSendResult doSend(MessageContent<?> content) throws MessageSendException {

        MailMessage message = (MailMessage) content;

        // 调用实际的邮件发送服务
        if (message.isContentMessage()) {

            emailSender.send(message.getToList(), message.getCcList(), message.getSubject(), message.getContent(), message.isHtml(), message.getAttachments());
        } else {

            emailSender.sendTemplate(message);
        }

        return MessageSendResult.success(message.getMessageId(), message.getBusinessId());
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.MAIL.equals(messageType);
    }
}
