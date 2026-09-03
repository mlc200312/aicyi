package io.github.aicyi.midware.web.exception;

import io.github.aicyi.commons.lang.exception.BaseException;
import io.github.aicyi.commons.lang.exception.SystemException;
import io.github.aicyi.commons.lang.model.Result;
import io.github.aicyi.commons.lang.CommonResultCode;
import io.github.aicyi.midware.web.filter.TraceIdFilter;
import io.github.aicyi.midware.web.log.WebRequestLogRecorder;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Min
 * @description 全局异常处理器，实现 API 接口统一异常响应，并记录异常请求信息日志
 * <p>
 * 统一返回 {@link Result}（Integer code）。HTTP 状态口径约定：
 * <ul>
 *     <li>传输级错误（请求体非法 JSON、方法不支持、媒体类型不支持、参数缺失/绑定失败/类型转换失败、无匹配处理器）→ 4xx 状态码 + 业务码</li>
 *     <li>业务级错误（{@link BaseException} 及子类，含鉴权 40101/40102/40300，以及业务常用于参数校验的 {@link IllegalArgumentException}）与未知异常 → HTTP 200 + 业务错误码，堆栈不对外泄露</li>
 * </ul>
 * 所有失败响应经 {@link #withTraceId} 回填 traceId，便于客户端关联服务端日志。
 * <p>
 * 注：404 处理需应用开启 spring.mvc.throw-exception-if-no-handler-found=true 方可生效。
 * @date 2021/5/2
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException 异常处理（HTTP 状态码200）
     * <p>
     * 业务层广泛用其做参数/前置条件校验，按业务级错误口径返回 HTTP 200 + 40001 业务码，
     * 与 {@link BaseException} 保持一致，避免同一类参数问题出现两种 HTTP 状态口径
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), e.getMessage()));
    }

    /**
     * 请求体解析失败（如非法 JSON）异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), "request body is not readable"));
    }

    /**
     * 请求方法不支持异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), e.getMessage()));
    }

    /**
     * 媒体类型不支持异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), e.getMessage()));
    }

    /**
     * BindException 异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {

        List<String> messageList = new ArrayList<>(5);

        e.getBindingResult().getAllErrors().forEach(err -> {

                    // 字段校验失败取字段名，对象级校验失败取对象名
                    String name = err instanceof FieldError
                            ? ((FieldError) err).getField()
                            : err.getObjectName();

                    messageList.add(name + ":" + err.getDefaultMessage());
                }
        );

        String message = String.join(",", messageList);

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message));
    }

    /**
     * ConstraintViolationException 异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {

        List<String> messageList = new ArrayList<>();

        e.getConstraintViolations().forEach(err -> messageList
                .add(resolvePropertyName(err.getPropertyPath()) + ":" + err.getMessage())
        );

        String message = String.join(",", messageList);

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message));
    }

    /**
     * MissingServletRequestParameterException 异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {

        String message = String.format("%s is required", e.getParameterName());

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message));
    }

    /**
     * 请求参数类型转换失败（如路径变量/查询参数无法转为目标类型）异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        String message = String.format("parameter [%s] type mismatch, expected %s",
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message));
    }

    /**
     * 路径变量缺失异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {

        String message = String.format("path variable [%s] is required", e.getVariableName());

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message));
    }

    /**
     * Servlet 请求绑定异常处理（如 @RequestHeader 缺失、cookie 绑定失败等）
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleServletRequestBindingException(ServletRequestBindingException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.PARAM_ERROR.getCode(), e.getMessage()));
    }

    /**
     * 无匹配处理器异常处理（需应用开启 throw-exception-if-no-handler-found）
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.NOT_FOUND));
    }

        /**
     * SystemException 异常处理（HTTP 状态码200）
     * <p>
     * 系统级异常统一返回通用 50001 文案，不回显异常携带的内部 message，避免内部细节外泄；
     * 完整堆栈由日志记录，供排查
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleSystemException(SystemException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.SYSTEM_ERROR));
    }

    /**
     * BaseException 异常处理（HTTP 状态码200）
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBaseException(BaseException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(e.getCode(), e.getMessage()));
    }

    /**
     * 未知异常兜底处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return withTraceId(Result.failure(CommonResultCode.SYSTEM_ERROR));
    }

    /**
     * 回填 traceId：取自 {@link TraceIdFilter} 写入 MDC 的当前请求链路 ID，缺失时保持为空
     */
    private static Result<Void> withTraceId(Result<Void> result) {
        result.setTraceId(MDC.get(TraceIdFilter.MDC_TRACE_ID_KEY));
        return result;
    }

        /**
     * 解析约束违反的属性名：取属性路径的叶子节点名，仅依赖标准 {@link Path} API，不耦合具体校验实现
     */
    private static String resolvePropertyName(Path propertyPath) {
        if (propertyPath == null) {
            return "";
        }

        String name = null;
        for (Path.Node node : propertyPath) {
            name = node.getName();
        }
        return name != null ? name : propertyPath.toString();
    }
}
