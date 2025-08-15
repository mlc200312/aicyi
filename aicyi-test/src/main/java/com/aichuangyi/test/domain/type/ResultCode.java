package com.aichuangyi.test.domain.type;

import com.aichuangyi.commons.lang.IEnumType;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 2023/8/1
 **/
public enum ResultCode implements IEnumType<Integer> {
    SYSTEM_ERROR(999999, "System error");

    private Integer code;
    private String description;

    ResultCode(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
