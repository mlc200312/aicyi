package io.github.aicyi.commons.security.token.exception;

/**
 * @author Mr.Min
 * @description Token无效异常
 * @date 15:05
 **/
public class TokenInvalidException extends TokenException {

    public TokenInvalidException(String message) {
        super(message);
    }

    public TokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
