package io.github.aicyi.example.listener;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.example.channel.MessageChannels;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class MessageHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlers.class);

    @StreamListener(MessageChannels.INPUT)
    public void handleMessage(String message) {
        LOGGER.info("Received message: " + message);
        // 处理消息逻辑
    }
}