package io.github.aicyi.commons.lang.type;

import io.github.aicyi.commons.lang.StringEnumType;

/**
 * @author Mr.Min
 * @description 模板引擎类型
 * @date 2026/5/28
 **/
public enum TemplateEngineType implements StringEnumType {

    /**
     * 简单占位符
     * 例如：
     * Hello ${name}
     */
    SIMPLE("Simple", "简单占位符"),

    /**
     * FreeMarker
     */
    FREEMARKER("FreeMarker", "FreeMarker"),

    /**
     * Thymeleaf
     */
    THYMELEAF("Thymeleaf", "Thymeleaf"),

    /**
     * Mustache
     */
    MUSTACHE("Mustache", "Mustache");

    private String code;
    private String description;

    TemplateEngineType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}