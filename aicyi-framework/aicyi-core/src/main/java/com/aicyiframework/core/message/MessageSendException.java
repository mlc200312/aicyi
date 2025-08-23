package com.aicyiframework.core.message;

/**
 * 消息发送异常
 */
public class MessageSendException extends Exception {
    private final String code;

    public MessageSendException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
