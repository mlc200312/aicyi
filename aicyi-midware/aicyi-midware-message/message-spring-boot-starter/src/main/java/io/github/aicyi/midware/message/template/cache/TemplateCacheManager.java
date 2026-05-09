package io.github.aicyi.midware.message.template.cache;

import io.github.aicyi.commons.lang.type.BooleanType;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.template.mapper.MessageTemplateMapper;
import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.midware.message.template.model.MessageTemplateExample;

import java.util.List;


/**
 * @author Mr.Min
 * @description 模版缓存管理器实现
 * @date 2026/5/7
 **/
public class TemplateCacheManager implements TemplateProvider {

    private final TemplateLocalCache localCache;

    private final TemplateRedisCache redisCache;

    private final MessageTemplateMapper templateMapper;

    public TemplateCacheManager(TemplateLocalCache localCache, TemplateRedisCache redisCache, MessageTemplateMapper templateMapper) {
        this.localCache = localCache;
        this.redisCache = redisCache;
        this.templateMapper = templateMapper;
    }

    @Override
    public MessageTemplate getTemplate(String templateCode) {

        // 1. 本地缓存
        MessageTemplate template = localCache.get(templateCode);

        if (template != null) {
            return template;
        }

        // 2. Redis
        template = redisCache.get(templateCode);

        if (template != null) {

            localCache.put(templateCode, template);

            return template;
        }

        // 3. DB
        MessageTemplateExample example = new MessageTemplateExample();
        MessageTemplateExample.Criteria criteria = example.createCriteria();
        criteria.andTemplateCodeEqualTo(templateCode).andDeletedEqualTo(BooleanType.FALSE);
        List<MessageTemplate> messageTemplateList = templateMapper.selectByExampleWithBLOBs(example);
        if (messageTemplateList != null && !messageTemplateList.isEmpty()) {
            template = messageTemplateList.get(0);
        }

        if (template == null) {
            return null;
        }

        // 回填缓存
        redisCache.put(template);

        localCache.put(templateCode, template);

        return template;
    }
}