package io.github.aicyi.midware.rabbitmq;

import io.github.aicyi.commons.core.exception.MessageSendException;
import io.github.aicyi.commons.core.message.AbstractMessageSender;
import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.core.message.MessageType;
import io.github.aicyi.commons.core.message.SendResult;
import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.Maps;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description Spring Cloud Stream 实现
 * @date 2025/9/10
 **/
public class StreamMessageSender implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(StreamMessageSender.class);

    private final StreamBridge streamBridge;

    public StreamMessageSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public boolean send(String destination, Object message) {
        return send(destination, message, Collections.emptyMap());
    }

    @Override
    public boolean send(String destination, Object message, Map<String, Object> properties) {
        try {
            Message<?> springMessage = MessageBuilder.withPayload(message)
                    .copyHeaders(properties)
                    .build();

            return streamBridge.send(destination, springMessage);
        } catch (Exception e) {
            logger.error(e, "发送MQ消息失败 - destination: {}, properties: {}", destination, properties);
            throw new MessageSendException("发送MQ消息失败:" + e.getMessage(), e);
        }
    }

    @Override
    public boolean sendDelayed(String destination, Object message, long delay) {
        return send(destination, message, Maps.of("x-delay", (Object) delay).build());
    }

    @Override
    public List<Boolean> sendBatch(String destination, List<?> messages) {
        return messages.stream()
                .map(msg -> send(destination, msg))
                .collect(Collectors.toList());
    }
}