package io.github.aicyi.commons.security.token.exception;

import io.github.aicyi.commons.core.exception.BaseException;
import io.github.aicyi.commons.lang.type.CommonResultCode;

/**
 * @author Mr.Min
 * @description Token解析异常
 * @date 15:05
 **/
public abstract class TokenException extends BaseException {

    public TokenException(String message) {
        super(CommonResultCode.UNAUTHORIZED.getCode(), message);
    }

    public TokenException(String message, Throwable cause) {
        super(CommonResultCode.UNAUTHORIZED.getCode(), message, cause);
    }
}
