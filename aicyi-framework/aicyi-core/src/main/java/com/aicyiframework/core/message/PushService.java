package com.aicyiframework.core.message;

import java.util.List;
import java.util.Map;

/**
 * 推送服务
 */
public interface PushService {
    String send(
            List<String> deviceTokens,
            String title,
            String content,
            Map<String, String> extras,
            PushPlatform platform) throws MessageSendException;
}
