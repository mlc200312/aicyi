package io.github.aicyi.commons.lang.exception;

import io.github.aicyi.commons.lang.CommonResultCode;

/**
 * @author Mr.Min
 * @description 未授权异常
 * @date 2026/4/23
 **/
public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super(CommonResultCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(CommonResultCode.UNAUTHORIZED.getCode(), message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(CommonResultCode.UNAUTHORIZED.getCode(), message, cause);
    }
}
