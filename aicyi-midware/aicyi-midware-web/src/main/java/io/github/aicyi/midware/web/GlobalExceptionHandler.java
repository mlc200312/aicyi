package io.github.aicyi.midware.web;

import io.github.aicyi.commons.lang.exception.BusinessException;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.commons.lang.type.CommonResultCode;
import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 实现 API 接口统一
 * @date 2021/5/2
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常处理
     *
     * @param ex the target exception
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public final Response<Void> handleBusinessException(BusinessException ex) {
        LOGGER.error(ex, "handleException cause: {}", ex.getMessage());
        return Response.failure(ex.getCodeAsString(), ex.getMessage());
    }

    /**
     * 业务异常处理
     *
     * @param ex the target exception
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    public final Response<Void> handleBusinessException(UnauthorizedException ex) {
        LOGGER.error(ex, "handleException cause: {}", ex.getMessage());
        return Response.failure(ex.getCodeAsString(), ex.getMessage());
    }

    /**
     * 参数异常处理
     *
     * @param ex the target exception
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public final Response<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.error(ex, "handleException cause: {}", ex.getMessage());
        return Response.failure(CommonResultCode.PARAM_ERROR);
    }


    /**
     * 参数异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> messageList = new ArrayList<>(5);
        Object request = ex.getBindingResult().getTarget();
        ex.getBindingResult().getAllErrors().forEach(err -> messageList
                .add(
                        ((DefaultMessageSourceResolvable) Objects.requireNonNull(err.getArguments())[0]).getDefaultMessage() + ":" + err.getDefaultMessage()
                )
        );
        String op = ex.getParameter().getContainingClass().getSimpleName() + "." + ex.getParameter().getMethod().getName();

        String message = String.join(",", messageList);

        LOGGER.error(ex, "handleException cause: {}：", ex.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * 参数异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({BindException.class})
    public Response handleBindException(BindException ex) {
        List<String> messageList = new ArrayList<>();
        Object request = ex.getBindingResult().getTarget();
        ex.getBindingResult().getAllErrors().forEach(err -> messageList
                .add(((DefaultMessageSourceResolvable) Objects.requireNonNull(err.getArguments())[0]).getDefaultMessage() + ":" + err.getDefaultMessage())
        );

        String message = String.join(",", messageList);

        LOGGER.error(ex, "handleException cause: {}：", ex.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * @param ex
     * @return
     * @NotEmpty String param
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public Response handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> messageList = new ArrayList<>();
        ex.getConstraintViolations().forEach(err -> messageList
                .add(((PathImpl) err.getPropertyPath()).getLeafNode().getName() + ":" + err.getMessage())
        );

        String message = String.join(",", messageList);

        LOGGER.error(ex, "handleException cause: {}：", ex.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * @param ex
     * @return
     * @RequestParam 异常
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public Response handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {

        String message = String.format("%s is required", ex.getParameterName());

        LOGGER.error(ex, "handleException cause: {}：", ex.getMessage());

        return Response.failure(String.valueOf(CommonResultCode.PARAM_ERROR.getCode()), message);
    }

    /**
     * 提供对标准Spring MVC异常的处理
     *
     * @param ex      the target exception
     * @param request the current request
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Response<Void>> handleException(Exception ex, WebRequest request) {

        LOGGER.error(ex, "handleException cause: {}", ex.getMessage());

        request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);

        return new ResponseEntity<>(Response.failure(CommonResultCode.SYSTEM_ERROR), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}