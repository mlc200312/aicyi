package com.aicyiframework.core.message;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 发送结果 - 使用建造者模式优化
 */
@Data
@Builder
public class SendResult {
    private boolean success;
    private String messageId;
    private String channelMessageId;
    private String errorCode;
    private String errorMsg;
    @Builder.Default
    private Date completeTime = new Date();
    
    public static SendResult success(String messageId, String channelMessageId) {
        return SendResult.builder()
            .success(true)
            .messageId(messageId)
            .channelMessageId(channelMessageId)
            .build();
    }
    
    public static SendResult failure(String errorCode, String errorMsg) {
        return SendResult.builder()
            .success(false)
            .errorCode(errorCode)
            .errorMsg(errorMsg)
            .build();
    }
}