package io.github.aicyi.midware.message.template.cache;


import io.github.aicyi.commons.core.cache.Cache;
import io.github.aicyi.midware.message.core.model.MessageTemplate;

/**
 * @author Mr.Min
 * @description 模版远程缓存
 * @date 2026/5/7
 **/
public class TemplateRemoteCache {

    private static final String KEY_PREFIX = "msg:template:";

    private final Cache<String, MessageTemplate> remoteCache;

    public TemplateRemoteCache(Cache<String, MessageTemplate> remoteCache) {
        this.remoteCache = remoteCache;
    }

    public MessageTemplate get(String templateCode) {

        return remoteCache.get(KEY_PREFIX + templateCode);
    }

    public void put(MessageTemplate template) {

        remoteCache.put(KEY_PREFIX + template.getTemplateCode(), template);
    }

    public void delete(String templateCode) {

        remoteCache.evict(KEY_PREFIX + templateCode);
    }
}