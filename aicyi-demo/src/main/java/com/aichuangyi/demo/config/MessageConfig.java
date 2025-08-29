package com.aichuangyi.demo.config;

import com.aicyiframework.core.mq.MqManager;
import com.aichuangyi.demo.channel.MessageChannels;
import com.aicyiframework.mq.StreamConfig;
import com.aicyiframework.mq.StreamMessageMqManager;
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
