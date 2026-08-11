package io.github.aicyi.commons.lang.exception;


/**
 * @author Mr.Min
 * @description Token已过期
 * @date 15:13
 **/
public class TokenExpiredException extends TokenException {

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
