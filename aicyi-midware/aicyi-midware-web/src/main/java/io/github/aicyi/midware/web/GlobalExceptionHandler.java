package io.github.aicyi.midware.web;

import io.github.aicyi.commons.lang.exception.BaseException;
import io.github.aicyi.commons.lang.type.CommonResultCode;
import io.github.aicyi.midware.web.util.WebRequestLogRecorder;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Min
 * @description 全局异常处理器，实现 API 接口统一异常响应，并记录异常请求信息日志
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
    public final ResponseEntity<Response<Void>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return new ResponseEntity<>(Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), e.getMessage()), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * BindException 异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler({BindException.class})
    public ResponseEntity<Response<Void>> handleBindException(BindException e, HttpServletRequest request) {

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

        return new ResponseEntity<>(Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * ConstraintViolationException 异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Response<Void>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {

        List<String> messageList = new ArrayList<>();

        e.getConstraintViolations().forEach(err -> messageList
                .add(((PathImpl) err.getPropertyPath()).getLeafNode().getName() + ":" + err.getMessage())
        );

        String message = String.join(",", messageList);

        WebRequestLogRecorder.logError(request, e);

        return new ResponseEntity<>(Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * MissingServletRequestParameterException 异常处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<Response<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {

        String message = String.format("%s is required", e.getParameterName());

        WebRequestLogRecorder.logError(request, e);

        return new ResponseEntity<>(Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message), null, HttpStatus.BAD_REQUEST);
    }

    /**
     * BaseException 异常处理
     *
     * @param e 异常
     */
    @ExceptionHandler(BaseException.class)
    public final ResponseEntity<Response<Void>> handleBaseException(BaseException e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return new ResponseEntity<>(Response.failure(e.getCodeAsString(), e.getMessage()), null, e.getStatus());
    }

    /**
     * 提供对未知异常的处理
     *
     * @param e       异常
     * @param request HTTP 请求
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Response<Void>> handleException(Exception e, HttpServletRequest request) {

        WebRequestLogRecorder.logError(request, e);

        return new ResponseEntity<>(Response.failure(CommonResultCode.SYSTEM_ERROR), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}