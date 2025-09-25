package io.github.aicyi.example.consumer.handler;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.example.domain.User;
import io.github.aicyi.example.channel.MessageChannels;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @author Mr.Min
 * @description Topic 消息处理
 * @date 2025/9/25
 **/
@Component
public class TopicMessageHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicMessageHandlers.class);

    @StreamListener(value = MessageChannels.ORDER_EVENTS_IN_0, condition = "headers['routingKey']=='order.created'")
    public void orderEventsCreated(org.springframework.messaging.Message<User> message) {

        MessageHeaders headers = message.getHeaders();

        Object object = headers.get("routingKey");

        LOGGER.info("Received message [{}]: {}", object, message.getPayload());
        // 处理消息逻辑
    }

    @StreamListener(value = MessageChannels.ORDER_EVENTS_IN_0, condition = "headers['routingKey']=='order.paid'")
    public void orderEventsPaid(org.springframework.messaging.Message<User> message) {

        MessageHeaders headers = message.getHeaders();

        Object object = headers.get("routingKey");

        LOGGER.info("Received message [{}]: {}", object, message.getPayload());
        // 处理消息逻辑
    }

    @StreamListener(value = MessageChannels.SYSTEM_LOGS_IN_0)
    public void systemLogs(org.springframework.messaging.Message<User> message) {

        MessageHeaders headers = message.getHeaders();

        Object object = headers.get("routingKey");

        LOGGER.info("Received message [{}]: {}", object, message.getPayload());
    }
}