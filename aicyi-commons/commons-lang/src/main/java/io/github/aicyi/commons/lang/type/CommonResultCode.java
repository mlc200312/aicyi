package io.github.aicyi.commons.lang.type;


import io.github.aicyi.commons.core.IResultCode;

/**
 * @author Mr.Min
 * @description 公共异常错误码枚举
 * @date 2026/4/21
 **/
public enum CommonResultCode implements IResultCode {
    SUCCESS(0, "Success"),

    BUSINESS_ERROR(20001, "Business Error"),

    PARAM_ERROR(40001, "Bad Request"),
    UNAUTHORIZED(40101, "Unauthorized"),
    FORBIDDEN(40302, "No Permission"),

    SYSTEM_ERROR(50000, "Internal Server Error");

    private final Integer code;
    private final String message;

    CommonResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}