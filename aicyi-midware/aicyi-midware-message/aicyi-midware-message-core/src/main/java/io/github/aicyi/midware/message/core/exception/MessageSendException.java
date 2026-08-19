package io.github.aicyi.midware.message.core.exception;

import io.github.aicyi.commons.lang.IResultCode;
import io.github.aicyi.commons.lang.exception.BaseException;

/**
 * @author Mr.Min
 * @description 消息发送异常：归入 commons 统一异常体系，错误码使用 {@link MessageResultCode} 枚举
 * @date 2025/8/25
 **/
public class MessageSendException extends BaseException {

    public MessageSendException(IResultCode resultCode) {
        super(resultCode);
    }

    public MessageSendException(IResultCode resultCode, String message) {
        super(resultCode.getCode(), message);
    }

    public MessageSendException(IResultCode resultCode, String message, Throwable cause) {
        super(resultCode.getCode(), message, cause);
    }
}
