package io.github.aicyi.commons.lang.model;

import io.github.aicyi.commons.lang.IResult;
import io.github.aicyi.commons.lang.IResultCode;
import io.github.aicyi.commons.lang.CommonResultCode;

/**
 * @author Mr.Min
 * @description 统一返回对象
 * @date 2026/4/21
 **/
public class Result<D> extends BaseBean implements IResult<Integer, D> {
    private Integer code;
    private String message;
    private D data;

    /**
     * 请求链路 traceId：由全局异常处理器从 MDC 回填，便于客户端拿失败响应直接关联服务端日志；
     * 无链路上下文时为空（响应头 X-Trace-Id 仍由 TraceIdFilter 统一输出）
     */
    private String traceId;

    /**
     * 保留无参构造器，支持 Jackson 等框架反序列化；业务代码请使用静态工厂方法创建
     */
    protected Result() {
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(Integer code, String message, D data) {
        this(code, message);
        this.data = data;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(CommonResultCode.SUCCESS.getCode(), CommonResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> failure(IResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> Result<T> failure(Integer code, String message) {
        return new Result<>(code, message);
    }
}