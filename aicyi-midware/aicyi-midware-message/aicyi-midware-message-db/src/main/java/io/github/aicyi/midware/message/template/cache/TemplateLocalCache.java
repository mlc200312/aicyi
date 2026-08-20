package io.github.aicyi.midware.message.template.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.aicyi.midware.message.core.model.MessageTemplate;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 模版本地缓存
 * @date 2026/5/7
 **/
public class TemplateLocalCache {

    private final Cache<String, MessageTemplate> cache =
            Caffeine.newBuilder()
                    .maximumSize(1000)
                    .expireAfterWrite(Duration.ofMinutes(30))
                    .build();

    public MessageTemplate get(String templateCode) {
        return cache.getIfPresent(templateCode);
    }

    public void put(String templateCode, MessageTemplate template) {
        cache.put(templateCode, template);
    }

    public void invalidate(String templateCode) {
        cache.invalidate(templateCode);
    }
}