package io.github.aicyi.midware.message.push;

import io.github.aicyi.commons.core.exception.MessageSendException;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 推送服务
 * @date 2025/8/25
 **/
public interface PushManager {
    String send(
            List<String> deviceTokens,
            String title,
            String content,
            Map<String, String> extras,
            PushPlatform platform) throws MessageSendException;
}
