package com.aichuangyi.example.config;

import com.aicyiframework.message.stream.MqManager;
import com.aichuangyi.example.channel.MessageChannels;
import com.aicyiframework.message.stream.StreamConfig;
import com.aicyiframework.message.stream.StreamMessageMqManager;
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
