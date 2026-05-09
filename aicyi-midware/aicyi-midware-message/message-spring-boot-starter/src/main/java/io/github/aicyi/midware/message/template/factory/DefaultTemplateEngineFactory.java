package io.github.aicyi.midware.message.template.factory;

import io.github.aicyi.midware.message.core.model.TemplateEngineType;
import io.github.aicyi.midware.message.core.template.TemplateEngine;
import io.github.aicyi.midware.message.core.template.TemplateEngineFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Min
 * @description 模版引擎工厂实现
 * @date 2025/8/25
 **/
public class DefaultTemplateEngineFactory implements TemplateEngineFactory {

    private Map<TemplateEngineType, TemplateEngine> engineMap = new ConcurrentHashMap<>();

    @Override
    public TemplateEngine getTemplateEngine(TemplateEngineType engineType) {
        TemplateEngine templateEngine = engineMap.get(engineType);
        if (templateEngine == null) {
            throw new IllegalArgumentException("未找到对应的模版引擎类型: " + engineType);
        }
        return templateEngine;
    }

    @Override
    public void register(TemplateEngineType engineType, TemplateEngine templateEngine) {
        engineMap.put(engineType, templateEngine);
    }
}
