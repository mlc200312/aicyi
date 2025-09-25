package io.github.aicyi.example.boot.config;

import io.github.aicyi.example.channel.MessageChannels;
import io.github.aicyi.midware.rabbitmq.*;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mr.Min
 * @description 消息配置
 * @date 15:43
 **/
@Configuration
@EnableBinding(MessageChannels.MessageOutput.class)
public class MessageConfig {

    @Bean
    public MessageSender messageSender(StreamBridge streamBridge) {
        return new StreamMessageSender(streamBridge);
    }
}
