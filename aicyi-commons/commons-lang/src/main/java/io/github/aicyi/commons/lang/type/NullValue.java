package io.github.aicyi.commons.lang.type;

import io.github.aicyi.commons.core.EnumType;

public enum NullValue implements EnumType {
    INSTANCE(0, "Null Value");

    private int code;
    private String description;

    NullValue(int code, String description) {
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