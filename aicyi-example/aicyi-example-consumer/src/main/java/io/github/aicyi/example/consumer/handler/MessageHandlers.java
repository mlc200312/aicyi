package io.github.aicyi.example.consumer.handler;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.example.domain.User;
import io.github.aicyi.example.channel.MessageChannels;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @author Mr.Min
 * @description 默认消息处理
 * @date 2025/9/25
 **/
@Component
public class MessageHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlers.class);

    @StreamListener(MessageChannels.INPUT)
    public void handleMessage(User message) {
        LOGGER.info("Received message: " + message);
        // 处理消息逻辑
    }
}