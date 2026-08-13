package io.github.aicyi.midware.web;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.lang.exception.BaseException;
import io.github.aicyi.commons.lang.exception.BusinessException;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.commons.lang.type.CommonResultCode;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.Assert;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Min
 * @description 实现 API 接口统一
 * @date 2021/5/2
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * IllegalArgumentException 异常处理
     *
     * @param e the target exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public final Response<Void> handleIllegalArgumentException(IllegalArgumentException e) {

        logger.error(e, "handleIllegalArgumentException cause: {}", e.getMessage());

        return Response.failure(CommonResultCode.PARAM_ERROR);
    }


    /**
     * MethodArgumentNotValidException 异常处理
     *
     * @param e
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<String> messageList = new ArrayList<>(5);

        Method method = e.getParameter().getMethod();

        Assert.notNull(method, "method");

        e.getBindingResult().getAllErrors().forEach(err -> {

                    Object[] arguments = err.getArguments();

                    Assert.notNull(arguments, "arguments");

                    messageList
                            .add(
                                    ((DefaultMessageSourceResolvable) arguments[0]).getDefaultMessage() + ":" + err.getDefaultMessage()
                            );
                }
        );

        String message = String.join(",", messageList);

        logger.error(e, "handleMethodArgumentNotValidException cause: {}：", e.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * BindException 异常处理
     *
     * @param e
     */
    @ExceptionHandler({BindException.class})
    public Response<Void> handleBindException(BindException e) {

        List<String> messageList = new ArrayList<>();

        e.getBindingResult().getAllErrors().forEach(err -> {

                    Object[] arguments = err.getArguments();

                    Assert.notNull(arguments, "arguments");

                    messageList
                            .add(((DefaultMessageSourceResolvable) arguments[0]).getDefaultMessage() + ":" + err.getDefaultMessage());
                }
        );

        String message = String.join(",", messageList);

        logger.error(e, "handleBindException cause: {}：", e.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * ConstraintViolationException 异常处理
     *
     * @param e
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public Response<Void> handleConstraintViolationException(ConstraintViolationException e) {

        List<String> messageList = new ArrayList<>();

        e.getConstraintViolations().forEach(err -> messageList
                .add(((PathImpl) err.getPropertyPath()).getLeafNode().getName() + ":" + err.getMessage())
        );

        String message = String.join(",", messageList);

        logger.error(e, "handleConstraintViolationException cause: {}：", e.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * MissingServletRequestParameterException 异常处理
     *
     * @param e
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public Response<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {

        String message = String.format("%s is required", e.getParameterName());

        logger.error(e, "handleMissingServletRequestParameterException cause: {}：", e.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * BaseException 异常处理
     *
     * @param e the target exception
     */
    @ExceptionHandler(BaseException.class)
    public final Response<Void> handleBaseException(BaseException e) {

        logger.error(e, "handleBaseException cause: {}", e.getMessage());

        return Response.failure(e.getCodeAsString(), e.getMessage());
    }

    /**
     * 提供对标准Spring MVC异常的处理
     *
     * @param e       the target exception
     * @param request the current request
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Response<Void>> handleException(Exception e, WebRequest request) {

        logger.error(e, "handleException cause: {}", e.getMessage());

        request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, e, WebRequest.SCOPE_REQUEST);

        return new ResponseEntity<>(Response.failure(CommonResultCode.SYSTEM_ERROR), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}