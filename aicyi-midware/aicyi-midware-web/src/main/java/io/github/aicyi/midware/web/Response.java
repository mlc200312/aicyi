package io.github.aicyi.midware.web;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.lang.IResponse;
import io.github.aicyi.commons.lang.IResultCode;
import io.github.aicyi.commons.lang.type.CommonResultCode;

/**
 * @author Mr.Min
 * @description 统一响应对象
 * @date 2026/4/22
 **/
public class Response<D> extends BaseBean implements IResponse<D> {
    private Long timestamp;
    private boolean status;
    private String code;
    private String message;
    private D data;

    public Response() {
        this.timestamp = System.currentTimeMillis();
    }

    public Response(String code, String message, boolean status) {
        this(code, message, null, status);
    }

    public Response(String code, String message, D data, boolean status) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static <D> Response<D> success() {
        return success(null);
    }

    public static <D> Response<D> success(D data) {
        return new Response<>(String.valueOf(CommonResultCode.SUCCESS.getCode()), CommonResultCode.SUCCESS.getMessage(), data, true);
    }

    public static <D> Response<D> failure(IResultCode resultCode) {
        return new Response<>(String.valueOf(resultCode.getCode()), resultCode.getMessage(), null, false);
    }

    public static <D> Response<D> failure(String code, String message) {
        return new Response<>(code, message, null, false);
    }
}
