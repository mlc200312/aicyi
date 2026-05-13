package io.github.aicyi.commons.security.token.exception;

/**
 * @author Mr.Min
 * @description Token解析异常
 * @date 15:05
 **/
public class TokenParseException extends TokenException {

    public TokenParseException(String message) {
        super(message);
    }

    public TokenParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
