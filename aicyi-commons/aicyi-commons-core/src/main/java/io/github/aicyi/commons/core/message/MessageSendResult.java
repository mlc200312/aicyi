package io.github.aicyi.commons.core.message;

import io.github.aicyi.commons.lang.model.BaseBean;

import java.time.LocalDateTime;

/**
 * @author Mr.Min
 * @description 消息发送结果：不可变对象，统一通过静态工厂方法创建
 * @date 2025/8/25
 **/
public class MessageSendResult extends BaseBean {

    /**
     * 未指定错误码时的缺省错误码
     */
    public static final String UNKNOWN_ERROR_CODE = "UNKNOWN_ERROR";

    private final boolean success;
    private final String messageId;
    private final String channelMessageId;
    private final String errorCode;
    private final String errorMsg;
    private final LocalDateTime completeTime;

    private MessageSendResult(boolean success, String messageId, String channelMessageId,
                              String errorCode, String errorMsg, LocalDateTime completeTime) {
        this.success = success;
        this.messageId = messageId;
        this.channelMessageId = channelMessageId;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.completeTime = completeTime;
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
     * 快速创建成功结果
     */
    public static MessageSendResult success(String messageId, String channelMessageId) {
        return new MessageSendResult(true, messageId, channelMessageId, null, null, LocalDateTime.now());
    }

    /**
     * 快速创建成功结果（仅消息ID）
     */
    public static MessageSendResult success(String messageId) {
        return success(messageId, null);
    }

    /**
     * 快速创建失败结果
     */
    public static MessageSendResult failure(String errorCode, String errorMsg) {
        return new MessageSendResult(false, null, null, errorCode, errorMsg, LocalDateTime.now());
    }

    /**
     * 快速创建失败结果（保留消息ID，便于关联原始消息排查）
     */
    public static MessageSendResult failure(String messageId, String errorCode, String errorMsg) {
        return new MessageSendResult(false, messageId, null, errorCode, errorMsg, LocalDateTime.now());
    }

    /**
     * 快速创建失败结果（仅错误消息，错误码使用 {@link #UNKNOWN_ERROR_CODE}）
     */
    public static MessageSendResult failure(String errorMsg) {
        return failure(UNKNOWN_ERROR_CODE, errorMsg);
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
     * 转换为成功结果（用于结果转换，保留原完成时间）
     */
    public MessageSendResult asSuccess(String newMessageId, String newChannelMessageId) {
        return new MessageSendResult(true, newMessageId, newChannelMessageId, null, null, this.completeTime);
    }

    /**
     * 转换为失败结果（用于结果转换，保留原消息ID与完成时间）
     */
    public MessageSendResult asFailure(String newErrorCode, String newErrorMsg) {
        return new MessageSendResult(false, this.messageId, null, newErrorCode, newErrorMsg, this.completeTime);
    }
}
