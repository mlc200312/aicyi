package com.aichuangyi.demo.listener;

import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aichuangyi.demo.channel.MessageChannels;
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