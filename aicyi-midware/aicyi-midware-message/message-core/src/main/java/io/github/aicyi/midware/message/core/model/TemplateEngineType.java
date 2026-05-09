package io.github.aicyi.midware.message.core.model;

public enum TemplateEngineType {

    /**
     * 简单占位符
     * 例如：
     * Hello ${name}
     */
    SIMPLE,

    /**
     * FreeMarker
     */
    FREEMARKER,

    /**
     * Thymeleaf
     */
    THYMELEAF,

    /**
     * Mustache
     */
    MUSTACHE
}