package io.github.aicyi.commons.core.template;


/**
 * @author Mr.Min
 * @description 模板引擎工厂
 * @date 18:06
 **/
public interface TemplateEngineFactory {

    /**
     * 获取模板引擎
     *
     * @param engineType 引擎类型
     * @return 模板引擎，未注册时由实现决定返回策略
     */
    TemplateEngine getTemplateEngine(TemplateEngineType engineType);

    /**
     * 注册模板引擎
     *
     * @param engineType     引擎类型
     * @param templateEngine 模板引擎实例
     */
    void register(TemplateEngineType engineType, TemplateEngine templateEngine);
}
