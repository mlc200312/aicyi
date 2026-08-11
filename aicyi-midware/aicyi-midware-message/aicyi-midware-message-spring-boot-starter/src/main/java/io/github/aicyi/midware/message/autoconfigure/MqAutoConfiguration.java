package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.midware.message.mq.sender.MqSender;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.rabbitmq.StreamMqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "aicyi.message.mq",
        name = "enabled",
        havingValue = "true")
public class MqAutoConfiguration {

    private final StreamBridge streamBridge;

    public MqAutoConfiguration(@Autowired(required = false) StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.mq",
            name = "provider",
            havingValue = "rabbitMq")
    public MqSender defaultMqSender() {
        return new StreamMqSender(streamBridge);
    }
}