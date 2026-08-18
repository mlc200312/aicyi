package io.github.aicyi.commons.lang.exception;

import io.github.aicyi.commons.lang.IResultCode;
import io.github.aicyi.commons.lang.type.CommonResultCode;

/**
 * @author Mr.Min
 * @description 业务异常类
 * @date 2026/4/21
 **/
public class BusinessException extends BaseException {

    public BusinessException(IResultCode resultCode) {
        super(resultCode);
    }

    public BusinessException(IResultCode resultCode, Throwable cause) {
        super(resultCode.getCode(), resultCode.getMessage(), cause);
    }

    public BusinessException(String message) {
        super(CommonResultCode.BUSINESS_ERROR.getCode(), message);
    }

    public BusinessException(String message, Throwable cause) {
        super(CommonResultCode.BUSINESS_ERROR.getCode(), message, cause);
    }
}