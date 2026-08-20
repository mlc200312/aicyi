package io.github.aicyi.midware.web.exception;

import io.github.aicyi.commons.lang.exception.BaseException;
import io.github.aicyi.commons.lang.model.Result;
import io.github.aicyi.commons.lang.CommonResultCode;
import io.github.aicyi.midware.web.log.WebRequestLogRecorder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Min
 * @description 全局异常处理器，实现 API 接口统一异常响应，并记录异常请求信息日志
 * <p>
 * 统一返回 {@link Result}（Integer code）；固定状态码的异常以 {@link ResponseStatus} 声明状态码，
 * 业务异常（{@link BaseException}）与未知异常统一返回 HTTP 200 并以业务码传达错误信息，堆栈不对外泄露
 * @date 2021/5/2
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException 异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return Result.failure(CommonResultCode.PARAM_ERROR.getCode(), e.getMessage());
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

        return Result.failure(CommonResultCode.PARAM_ERROR.getCode(), "request body is not readable");
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

        return Result.failure(CommonResultCode.PARAM_ERROR.getCode(), e.getMessage());
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

        return Result.failure(CommonResultCode.PARAM_ERROR.getCode(), e.getMessage());
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

        return Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message);
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

        return Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message);
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

        return Result.failure(CommonResultCode.PARAM_ERROR.getCode(), message);
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

        return Result.failure(e.getCode(), e.getMessage());
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

        return Result.failure(CommonResultCode.SYSTEM_ERROR);
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
