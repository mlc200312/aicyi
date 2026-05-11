package io.github.aicyi.commons.lang.exception;

import io.github.aicyi.commons.core.IResultCode;
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

    public BusinessException(Integer code, String message) {
        super(code, message);
    }

    public BusinessException(String message) {
        super(CommonResultCode.BUSINESS_ERROR.getCode(), message);
    }
}