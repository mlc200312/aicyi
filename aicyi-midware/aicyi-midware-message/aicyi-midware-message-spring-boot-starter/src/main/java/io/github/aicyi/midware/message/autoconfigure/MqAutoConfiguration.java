package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.midware.message.mq.adapter.MqMessageSender;
import io.github.aicyi.midware.message.mq.sender.MqSender;
import io.github.aicyi.midware.rabbitmq.StreamMqSender;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = MessageAutoConfiguration.class)
@ConditionalOnClass({StreamBridge.class, StreamMqSender.class})
@ConditionalOnProperty(
        prefix = "aicyi.message.mq",
        name = "enabled",
        havingValue = "true")
public class MqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StreamBridge.class)
    @ConditionalOnProperty(
            prefix = "aicyi.message.mq",
            name = "provider",
            havingValue = "rabbitMq")
    public MqSender defaultMqSender(StreamBridge streamBridge) {
        // StreamBridge 缺失时由 @ConditionalOnBean 跳过装配，避免以 null 构造发送器
        return new StreamMqSender(streamBridge);
    }

    /**
     * MQ 渠道适配器：包装 {@link MqSender} 接入统一消息框架。
     * 由本渠道配置自行装配，避免 MessageAutoConfiguration 直接引用渠道类
     */
    @Bean
    @ConditionalOnBean(MqSender.class)
    public MessageSender mqMessageSender(MqSender mqSender) {
        return new MqMessageSender(mqSender);
    }
}
