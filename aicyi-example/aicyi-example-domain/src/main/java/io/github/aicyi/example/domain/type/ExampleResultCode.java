package io.github.aicyi.example.domain.type;

import io.github.aicyi.commons.lang.ICodeType;
import io.github.aicyi.commons.lang.IResultCode;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 11:47
 **/
public enum ExampleResultCode implements IResultCode {
    OBJECT_NOT_FOUND(200102, "对象不存在");

    private final Integer code;
    private final String message;

    ExampleResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
