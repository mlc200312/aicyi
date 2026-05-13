package io.github.aicyi.commons.core.exception;

import io.github.aicyi.commons.core.IResultCode;

/**
 * @author Mr.Min
 * @description 业务异常类
 * @date 2026/4/21
 **/
public abstract class BaseException extends RuntimeException {

    private final Integer code;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(IResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }

    public Integer getCode() {
        return code;
    }

    public String getCodeAsString() {
        return String.valueOf(code);
    }
}