package io.github.aicyi.commons.core.template;

import io.github.aicyi.commons.lang.type.TemplateEngineType;

/**
 * @author Mr.Min
 * @description 模板引擎工厂
 * @date 18:06
 **/
public interface TemplateEngineFactory {

    /**
     * 获取模板引擎
     *
     * @param engineType
     * @return
     */
    TemplateEngine getTemplateEngine(TemplateEngineType engineType);

    /**
     * 注册模板引擎
     *
     * @param engineType
     * @param templateEngine
     */
    void register(TemplateEngineType engineType, TemplateEngine templateEngine);
}
