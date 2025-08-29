package com.aicyiframework.mq;

import com.aichuangyi.commons.util.id.IdGenerator;
import com.aicyiframework.core.message.MqMessage;
import com.aicyiframework.core.message.SendResult;
import com.aicyiframework.core.mq.MqManager;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class StreamMessageMqManager implements MqManager {

    private final StreamBridge streamBridge;
    private final StreamConfig streamConfig;

    public StreamMessageMqManager(StreamBridge streamBridge, StreamConfig streamConfig) {
        this.streamBridge = streamBridge;
        this.streamConfig = streamConfig;
    }

    @Override
    public <T> SendResult send(T message) {
        return send(message, streamConfig.getDefaultGroup());
    }

    @Override
    public <T> SendResult send(T message, String routingKey) {
        return send(message, streamConfig.getDefaultDestination(), routingKey);
    }

    @Override
    public <T> SendResult send(T message, String exchange, String routingKey) {
        try {
            Message<?> streamMessage = convertToStreamMessage(message, routingKey);

            // 使用 StreamBridge 发送消息
            boolean sent = streamBridge.send(exchange, streamMessage);

            if (sent) {
                return SendResult.success(getMessageId(streamMessage));
            } else {
                return SendResult.failure("消息发送失败，可能绑定未就绪");
            }
        } catch (Exception e) {
            return SendResult.failure("发送消息失败: " + e.getMessage());
        }
    }

    @Override
    public <T> SendResult sendDelayed(T message, long delayMillis) {
        return sendDelayed(message, streamConfig.getDefaultDestination(), delayMillis);
    }

    @Override
    public <T> SendResult sendDelayed(T message, String routingKey, long delayMillis) {
        try {
            MqMessage wrappedMessage = wrapMessage(message);
            wrappedMessage.getProperties().put("x-delay", delayMillis);
            wrappedMessage.getProperties().put("x-delay-routing-key", routingKey);

            return send(wrappedMessage, "delayed-exchange", routingKey);
        } catch (Exception e) {
            return SendResult.failure("发送延迟消息失败: " + e.getMessage());
        }
    }

    /**
     * 使用 Reactive Stream 发送消息
     */
    public <T> Mono<SendResult> sendReactive(T message, String destination) {
        return Mono.fromCallable(() -> send(message, destination)).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 发送带分区键的消息
     */
    public <T> SendResult sendWithPartition(T message, String partitionKey) {
        try {
            Message<?> streamMessage = convertToStreamMessage(message, null);
            streamMessage.getHeaders().put("partitionKey", partitionKey);

            boolean sent = streamBridge.send(streamConfig.getDefaultDestination(), streamMessage);

            return sent ? SendResult.success(getMessageId(streamMessage)) : SendResult.failure("分区消息发送失败");
        } catch (Exception e) {

            return SendResult.failure("发送分区消息失败: " + e.getMessage());
        }
    }

    private <T> Message<?> convertToStreamMessage(T message, String routingKey) {
        MessageBuilder<?> builder;
        if (message instanceof MqMessage) {
            MqMessage mqMessage = (MqMessage) message;

            builder = MessageBuilder.withPayload(mqMessage.getContent());

            builder.setHeader("messageId", mqMessage.getMessageId())
                    .setHeader("timestamp", mqMessage.getTimestamp())
                    .setHeader("routingKey", routingKey);

            if (mqMessage.getProperties() != null) {
                mqMessage.getProperties().forEach(builder::setHeader);
            }

            if (mqMessage.getTopic() != null) {
                builder.setHeader("topic", mqMessage.getTopic());
            }

            if (mqMessage.getTag() != null) {
                builder.setHeader("tag", mqMessage.getTag());
            }
        } else {
            builder = MessageBuilder.withPayload(message);

            builder.setHeader("messageId", IdGenerator.generateV7Id())
                    .setHeader("routingKey", routingKey);
        }
        return builder.build();
    }

    private <T> MqMessage wrapMessage(T message) {
        MqMessage mqMessage;
        if (message instanceof MqMessage) {
            mqMessage = (MqMessage) message;
        } else {
            mqMessage = MqMessage.of(message, null);
        }
        mqMessage.getProperties().put("source", "stream-sender");
        return mqMessage;
    }

    private String getMessageId(Message<?> message) {
        return (String) message.getHeaders().get("messageId");
    }
}