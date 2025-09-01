package com.aichuangyi.commons.core.message;

import com.aichuangyi.commons.lang.BaseBean;

import java.time.LocalDateTime;

/**
 * @author Mr.Min
 * @description 发送结果 - 使用建造者模式优化
 * @date 2025/8/25
 **/
public class SendResult extends BaseBean {
    private final boolean success;
    private final String messageId;
    private final String channelMessageId;
    private final String errorCode;
    private final String errorMsg;
    private final LocalDateTime completeTime;

    // 私有构造函数，只能通过Builder创建
    private SendResult(Builder builder) {
        this.success = builder.success;
        this.messageId = builder.messageId;
        this.channelMessageId = builder.channelMessageId;
        this.errorCode = builder.errorCode;
        this.errorMsg = builder.errorMsg;
        this.completeTime = builder.completeTime;
    }

    // Getter 方法
    public boolean isSuccess() {
        return success;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getChannelMessageId() {
        return channelMessageId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private boolean success;
        private String messageId;
        private String channelMessageId;
        private String errorCode;
        private String errorMsg;
        private LocalDateTime completeTime;

        public Builder() {
            this.completeTime = LocalDateTime.now(); // 默认使用当前时间
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder channelMessageId(String channelMessageId) {
            this.channelMessageId = channelMessageId;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public Builder completeTime(LocalDateTime completeTime) {
            this.completeTime = completeTime;
            return this;
        }

        /**
         * 构建 SendResult 实例（成功结果）
         */
        public SendResult buildSuccess(String messageId, String channelMessageId) {
            this.success = true;
            this.messageId = messageId;
            this.channelMessageId = channelMessageId;
            this.errorCode = null;
            this.errorMsg = null;
            return new SendResult(this);
        }

        /**
         * 构建 SendResult 实例（失败结果）
         */
        public SendResult buildFailure(String errorCode, String errorMsg) {
            this.success = false;
            this.channelMessageId = null;
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
            return new SendResult(this);
        }

        /**
         * 构建 SendResult 实例（自定义构建）
         */
        public SendResult build() {
            return new SendResult(this);
        }
    }

    /**
     * 静态工厂方法，创建Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 快速创建成功结果
     */
    public static SendResult success(String messageId, String channelMessageId) {
        return builder()
                .success(true)
                .messageId(messageId)
                .channelMessageId(channelMessageId)
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 快速创建成功结果（仅消息ID）
     */
    public static SendResult success(String messageId) {
        return success(messageId, null);
    }

    /**
     * 快速创建失败结果
     */
    public static SendResult failure(String errorCode, String errorMsg) {
        return builder()
                .success(false)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 快速创建失败结果（仅错误消息）
     */
    public static SendResult failure(String errorMsg) {
        return failure("UNKNOWN_ERROR", errorMsg);
    }

    /**
     * 是否有错误信息
     */
    public boolean hasError() {
        return !success && (errorCode != null || errorMsg != null);
    }

    /**
     * 获取错误信息（完整格式）
     */
    public String getFullError() {
        if (!hasError()) {
            return null;
        }
        if (errorCode != null && errorMsg != null) {
            return errorCode + ": " + errorMsg;
        }
        return errorMsg != null ? errorMsg : errorCode;
    }

    /**
     * 是否有渠道消息ID
     */
    public boolean hasChannelMessageId() {
        return channelMessageId != null && !channelMessageId.trim().isEmpty();
    }

    /**
     * 是否包含消息ID
     */
    public boolean hasMessageId() {
        return messageId != null && !messageId.trim().isEmpty();
    }

    /**
     * 转换为成功结果（用于结果转换）
     */
    public SendResult asSuccess(String newMessageId, String newChannelMessageId) {
        return builder()
                .success(true)
                .messageId(newMessageId)
                .channelMessageId(newChannelMessageId)
                .completeTime(this.completeTime)
                .build();
    }

    /**
     * 转换为失败结果（用于结果转换）
     */
    public SendResult asFailure(String newErrorCode, String newErrorMsg) {
        return builder()
                .success(false)
                .messageId(this.messageId)
                .errorCode(newErrorCode)
                .errorMsg(newErrorMsg)
                .completeTime(this.completeTime)
                .build();
    }
}