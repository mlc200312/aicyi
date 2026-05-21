package io.github.aicyi.commons.lang.exception;

/**
 * @author Mr.Min
 * @description Snowflake异常
 * @date 2026/5/21
 **/
public class SnowflakeException extends RuntimeException {

    public SnowflakeException(String message) {
        super(message);
    }

    public SnowflakeException(String message, Throwable cause) {
        super(message, cause);
    }
}