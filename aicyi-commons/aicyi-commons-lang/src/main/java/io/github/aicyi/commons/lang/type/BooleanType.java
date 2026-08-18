package io.github.aicyi.commons.lang.type;

import io.github.aicyi.commons.lang.EnumType;

/**
 * @author Mr.Min
 * @description 布尔类型枚举
 * @date 13:13
 **/
public enum BooleanType implements EnumType {
    FALSE(0, "否"),
    TRUE(1, "是");

    private final int code;
    private final String description;

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
