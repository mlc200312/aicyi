package io.github.aicyi.commons.lang.exception;


import io.github.aicyi.commons.lang.type.CommonResultCode;

/**
 * @author Mr.Min
 * @description Token已过期（独立错误码 40102，与 token 无效 40101 区分）
 * @date 15:13
 **/
public class TokenExpiredException extends TokenException {

    public TokenExpiredException(String message) {
        super(CommonResultCode.TOKEN_EXPIRED, message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(CommonResultCode.TOKEN_EXPIRED, message, cause);
    }
}
