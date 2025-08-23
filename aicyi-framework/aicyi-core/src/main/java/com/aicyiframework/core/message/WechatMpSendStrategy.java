package com.aicyiframework.core.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WechatMpSendStrategy implements MessageSendStrategy {

    private final WechatMpService wechatMpService;

    public WechatMpSendStrategy(WechatMpService wechatMpService) {
        this.wechatMpService = wechatMpService;
    }

    @Override
    public boolean supports(MessageType messageType) {
        return MessageType.WECHAT_MP == messageType;
    }

    @Override
    public SendResult send(Message message) {
        if (!(message instanceof WechatMpMessage)) {
            return SendResult.failure("TYPE_MISMATCH",
                    "Expected WechatMpMessage but got " + message.getClass().getSimpleName());
        }

        WechatMpMessage mpMessage = (WechatMpMessage) message;
        try {
            String channelId = wechatMpService.sendTemplateMessage(
                    mpMessage.getOpenIds(),
                    mpMessage.getTemplateId(),
                    mpMessage.getData(),
                    mpMessage.getPage(),
                    mpMessage.getMiniprogramState());

            return SendResult.success(mpMessage.getMessageId(), channelId);
        } catch (MessageSendException e) {
            return SendResult.failure(e.getCode(), e.getMessage());
        }
    }
}