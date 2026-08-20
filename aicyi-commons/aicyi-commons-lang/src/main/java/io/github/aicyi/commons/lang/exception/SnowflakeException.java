package io.github.aicyi.commons.lang.exception;

import io.github.aicyi.commons.lang.CommonResultCode;

/**
 * @author Mr.Min
 * @description Snowflake异常
 * @date 2026/5/21
 **/
public class SnowflakeException extends BaseException {

    public SnowflakeException(String message) {
        super(CommonResultCode.SYSTEM_ERROR.getCode(), message);
    }

    public SnowflakeException(String message, Throwable cause) {
        super(CommonResultCode.SYSTEM_ERROR.getCode(), message, cause);
    }
}