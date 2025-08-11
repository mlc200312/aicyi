package com.aichuangyi.commons.lang.type;

import com.aichuangyi.commons.EnumType;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 13:13
 **/
public enum BooleanType implements EnumType {
    FALSE(0, "否"),
    TRUE(1, "是");

    private int code;
    private String description;

    BooleanType(int code, String description) {
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
