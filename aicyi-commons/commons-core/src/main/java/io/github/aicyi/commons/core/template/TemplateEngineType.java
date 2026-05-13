package io.github.aicyi.commons.core.template;

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