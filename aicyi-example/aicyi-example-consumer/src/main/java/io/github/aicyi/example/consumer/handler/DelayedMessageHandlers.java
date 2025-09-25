package io.github.aicyi.example.consumer.handler;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.example.domain.User;
import io.github.aicyi.example.service.channel.MessageChannels;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @author Mr.Min
 * @description 延迟消息处理
 * @date 2025/9/25
 **/
@Component
public class DelayedMessageHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedMessageHandlers.class);

    @StreamListener(MessageChannels.DELAYED_INPUT)
    public void handleMessage(org.springframework.messaging.Message<User> message) {
        MessageHeaders headers = message.getHeaders();

        Object object = headers.get("amqp_receivedRoutingKey");

        LOGGER.info("Received message [{}]: {}", object, message.getPayload());
        // 处理消息逻辑
    }
}