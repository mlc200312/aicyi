package io.github.aicyi.commons.lang.exception;

import io.github.aicyi.commons.lang.IResultCode;

/**
 * @author Mr.Min
 * @description 异常基类：持有错误码，子类为各业务/系统异常
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

    /**
     * 从错误码推导 HTTP 状态码（取前 3 位，错误码规范为 3 位 HTTP 段 + 2 位序号）；
     * 错误码位数不足或非法时返回 null，由调用方决定回退策略。
     * <p>
     * 消费场景：供网关/跨系统调用方由异常错误码推导 HTTP 状态码。
     * 应用内 REST 接口由全局异常处理器统一返回 HTTP 200 + 业务错误码（REST 规范），不消费本方法
     */
    public Integer getStatus() {
        if (code == null) {
            return null;
        }
        String codeAsString = getCodeAsString();
        if (codeAsString.length() < 3) {
            return null;
        }
        try {
            return Integer.valueOf(codeAsString.substring(0, 3));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}