package io.github.aicyi.example.config;

import io.github.aicyi.example.channel.MessageChannels;
import io.github.aicyi.midware.rabbitmq.MqManager;
import io.github.aicyi.midware.rabbitmq.StreamConfig;
import io.github.aicyi.midware.rabbitmq.StreamMessageMqManager;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:43
 **/
@Configuration
@EnableBinding(MessageChannels.class)
public class MessageConfig {

    @Bean
    public MqManager mqManager(StreamBridge streamBridge) {
        return new StreamMessageMqManager(streamBridge, new StreamConfig());
    }
}
