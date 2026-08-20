package io.github.aicyi.commons.lang.exception;

import io.github.aicyi.commons.lang.IResultCode;
import io.github.aicyi.commons.lang.CommonResultCode;

/**
 * @author Mr.Min
 * @description Token解析异常
 * @date 15:05
 **/
public abstract class TokenException extends BaseException {

    protected TokenException(IResultCode resultCode, String message) {
        super(resultCode.getCode(), message);
    }

    protected TokenException(IResultCode resultCode, String message, Throwable cause) {
        super(resultCode.getCode(), message, cause);
    }

    public TokenException(String message) {
        this(CommonResultCode.UNAUTHORIZED, message);
    }

    public TokenException(String message, Throwable cause) {
        this(CommonResultCode.UNAUTHORIZED, message, cause);
    }
}
