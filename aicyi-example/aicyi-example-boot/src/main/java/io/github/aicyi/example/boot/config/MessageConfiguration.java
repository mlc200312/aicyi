package io.github.aicyi.example.boot.config;

import io.github.aicyi.example.service.channel.MessageChannels;
import io.github.aicyi.midware.message.sms.SmsProperties;
import io.github.aicyi.midware.kit.TwilioSmsManager;
import io.github.aicyi.midware.message.sms.SmsManager;
import io.github.aicyi.midware.rabbitmq.*;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description MQ消息相关配置
 * @date 15:43
 **/
@Configuration
@EnableBinding(MessageChannels.MessageOutput.class)
public class MessageConfiguration {

    @Bean
    public MqManager mqManager(StreamBridge streamBridge) {
        return new StreamMqManager(streamBridge);
    }

//    @Bean
//    public SmsManager smsManager(EmailManager emailManager) {
//        Map<String, String> template = new HashMap<>();
//        template.put("1", "hi");
//        EmailToSmsManager emailToSmsManager = new EmailToSmsManager(template);
//        emailToSmsManager.setEmailManager(emailManager);
//        return emailToSmsManager;
//    }

    @Bean
    public SmsManager smsManager(SmsProperties properties) {
        Map<String, String> template = new HashMap<>();
        template.put("1", "hi");
        TwilioSmsManager twilioSmsManager = new TwilioSmsManager(properties, template);
        return twilioSmsManager;
    }
}
