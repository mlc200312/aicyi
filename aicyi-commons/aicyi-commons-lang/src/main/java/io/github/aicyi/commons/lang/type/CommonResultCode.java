package io.github.aicyi.commons.lang.type;

import io.github.aicyi.commons.lang.IResultCode;

/**
 * @author Mr.Min
 * @description 公共异常错误码枚举
 * <p>
 * 码段规范：SUCCESS 固定为 0；错误码为 5 位整数，前 3 位对齐 HTTP 状态码段，后 2 位为段内序号（从 01 起），
 * 便于从错误码直接推导 HTTP 状态码（如 40001 → 400）；业务自定义错误码需遵守同一规则。
 * <p>
 * 业务错误归入 4xx 段（40002 为默认业务错误码），500xx 仅保留给系统级错误，
 * 避免业务校验失败被网关/监控误判为服务端故障
 * @date 2026/4/21
 **/
public enum CommonResultCode implements IResultCode {
    SUCCESS(0, "Success"),

    PARAM_ERROR(40001, "Bad Request"),
    BUSINESS_ERROR(40002, "Business Error"),
    UNAUTHORIZED(40101, "Unauthorized"),
    FORBIDDEN(40301, "No Permission"),
    NOT_FOUND(40401, "Not Found"),

    SYSTEM_ERROR(50001, "Internal Server Error");

    private final Integer code;
    private final String message;

    CommonResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}