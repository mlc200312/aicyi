package com.aicyiframework.core.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsSendStrategy implements MessageSendStrategy {

    private final SmsService smsService;

    public SmsSendStrategy(SmsService smsService) {
        this.smsService = smsService;
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.SMS == messageType;
    }

    @Override
    public SendResult send(Message message) {
        if (!(message instanceof SmsMessage)) {
            return SendResult.failure("TYPE_MISMATCH",
                    "Expected SmsMessage but got " + message.getClass().getSimpleName());
        }

        SmsMessage sms = (SmsMessage) message;
        try {
            String channelId = smsService.send(
                    sms.getPhoneNumbers(),
                    sms.getTemplateCode(),
                    sms.getTemplateParams(),
                    sms.getSignName());

            return SendResult.success(sms.getMessageId(), channelId);
        } catch (MessageSendException e) {
            return SendResult.failure(e.getCode(), e.getMessage());
        }
    }
}