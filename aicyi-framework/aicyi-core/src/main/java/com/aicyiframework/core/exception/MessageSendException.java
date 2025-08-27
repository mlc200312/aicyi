package com.aicyiframework.core.exception;

/**
 * @author Mr.Min
 * @description 消息发送异常
 * @date 2025/8/25
 **/
public class MessageSendException extends RuntimeException {
    private final String code;


    public MessageSendException(String message, Throwable cause) {
        super(message, cause);
        this.code = "UNKNOWN_ERROR";
    }

    public MessageSendException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
