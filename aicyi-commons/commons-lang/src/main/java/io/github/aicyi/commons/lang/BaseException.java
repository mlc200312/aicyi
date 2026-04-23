package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description 业务异常类
 * @date 2026/4/21
 **/
public abstract class BaseException extends RuntimeException {

    private final Integer code;

    public BaseException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getCodeAsString() {
        return String.valueOf(code);
    }
}