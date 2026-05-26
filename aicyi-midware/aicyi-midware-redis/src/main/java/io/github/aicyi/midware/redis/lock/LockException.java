package io.github.aicyi.midware.redis.lock;

import io.github.aicyi.commons.lang.exception.BaseException;
import io.github.aicyi.commons.lang.type.CommonResultCode;

public class LockException extends BaseException {

    public LockException(String message) {
        super(CommonResultCode.SYSTEM_ERROR.getCode(), message);
    }

    public LockException(String message, Throwable cause) {
        super(CommonResultCode.SYSTEM_ERROR.getCode(), message, cause);
    }
}