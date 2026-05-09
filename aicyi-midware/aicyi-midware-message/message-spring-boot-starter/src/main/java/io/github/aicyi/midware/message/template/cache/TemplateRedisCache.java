package io.github.aicyi.midware.message.template.cache;


import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.midware.redis.cache.RedisCacheManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 模版Redis缓存
 * @date 2026/5/7
 **/
public class TemplateRedisCache {

    private static final String KEY_PREFIX = "msg:template:";

    private final RedisCacheManager redisCacheManager;

    public TemplateRedisCache(RedisCacheManager redisCacheManage) {
        this.redisCacheManager = redisCacheManage;
    }

    public MessageTemplate get(String templateCode) {

        return (MessageTemplate) redisCacheManager.get(KEY_PREFIX + templateCode);
    }

    public void put(MessageTemplate template) {

        redisCacheManager.put(KEY_PREFIX + template.getTemplateCode(), template, 6, TimeUnit.HOURS);
    }

    public void delete(String templateCode) {

        redisCacheManager.remove(KEY_PREFIX + templateCode);
    }
}