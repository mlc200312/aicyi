package io.github.aicyi.commons.lang.exception;

import io.github.aicyi.commons.lang.IResultCode;
import io.github.aicyi.commons.lang.type.CommonResultCode;

/**
 * @author Mr.Min
 * @description 系统异常类：承载基础组件/中间件层的系统级错误（500xx 段），
 * 与业务异常 {@link BusinessException} 区分，便于全局异常处理器分级处理
 * @date 2026/8/18
 **/
public class SystemException extends BaseException {

    public SystemException(IResultCode resultCode) {
        super(resultCode);
    }

    public SystemException(IResultCode resultCode, Throwable cause) {
        super(resultCode.getCode(), resultCode.getMessage(), cause);
    }

    public SystemException(String message) {
        super(CommonResultCode.SYSTEM_ERROR.getCode(), message);
    }

    public SystemException(String message, Throwable cause) {
        super(CommonResultCode.SYSTEM_ERROR.getCode(), message, cause);
    }
}
