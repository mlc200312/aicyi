package io.github.aicyi.midware.web;

/**
 * @author Mr.Min
 * @description 异常错误类
 * @date 2026/4/21
 **/
public enum ResultCode {

    SUCCESS(0, "Success"),

    BUSINESS_ERROR(20001, "Business Error"),

    PARAM_ERROR(40001, "Bad Request"),
    UNAUTHORIZED(40101, "Unauthorized"),
    FORBIDDEN(40302, "No Permission"),

    SYSTEM_ERROR(50000, "Internal Server Error");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}