package com.aicyiframework.core.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailSendStrategy implements MessageSendStrategy {

    private final EmailService emailService;

    public EmailSendStrategy(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.EMAIL == messageType;
    }

    @Override
    public SendResult send(Message message) {
        if (!(message instanceof EmailMessage)) {
            return SendResult.failure("TYPE_MISMATCH",
                    "Expected EmailMessage but got " + message.getClass().getSimpleName());
        }

        EmailMessage email = (EmailMessage) message;
        try {
            String channelId = emailService.send(
                    email.getTo(),
                    email.getSubject(),
                    email.getContent(),
                    email.isHtml(),
                    email.getAttachments());

            return SendResult.success(email.getMessageId(), channelId);
        } catch (MessageSendException e) {
            return SendResult.failure(e.getCode(), e.getMessage());
        }
    }
}