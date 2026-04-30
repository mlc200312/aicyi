package io.github.aicyi.midware.rabbitmq;

import io.github.aicyi.commons.core.message.MessageSendException;
import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.midware.message.mq.MqSender;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description Spring Cloud Stream 实现
 * @date 2025/9/10
 **/
public class StreamMqSender implements MqSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamMqSender.class);

    private final StreamBridge streamBridge;

    public StreamMqSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public boolean send(String channel, Object payload) {
        return send(channel, payload, Collections.emptyMap());
    }

    @Override
    public boolean send(String channel, Object payload, Map<String, Object> headers) {
        try {
            Message<?> springMessage = MessageBuilder.withPayload(payload)
                    .copyHeaders(headers)
                    .build();

            return streamBridge.send(channel, springMessage);
        } catch (Exception e) {
            LOGGER.error(e, "发送MQ消息失败 - destination: {}, properties: {}", channel, headers);
            throw new MessageSendException("发送MQ消息失败:" + e.getMessage(), e);
        }
    }

    @Override
    public boolean sendDelayed(String channel, Object payload, long delayMillis) {
        return send(channel, payload, Maps.of("x-delay", (Object) delayMillis).build());
    }

    @Override
    public List<Boolean> sendBatch(String channel, List<?> payloads) {
        return payloads.stream()
                .map(msg -> send(channel, msg))
                .collect(Collectors.toList());
    }
}