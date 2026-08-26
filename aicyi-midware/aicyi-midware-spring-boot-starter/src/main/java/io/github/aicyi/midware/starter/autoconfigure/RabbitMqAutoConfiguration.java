package io.github.aicyi.midware.starter.autoconfigure;

import io.github.aicyi.midware.message.mq.sender.MqSender;
import io.github.aicyi.midware.rabbitmq.StreamMqSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({StreamBridge.class, StreamMqSender.class})
@ConditionalOnProperty(
        prefix = "aicyi.mq.rabbitmq",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class RabbitMqAutoConfiguration {

    /**
     * 默认 MQ 发送器。
     * <p>
     * StreamBridge 依赖改用 {@link ObjectProvider} 运行时注入，替代 @ConditionalOnBean 条件：
     * 方法参数在 Bean 实例化期解析，此时所有自动配置的 Bean 定义均已注册完毕，
     * 天然规避 @ConditionalOnBean 的评估顺序陷阱，无需声明与
     * FunctionConfiguration 的 after 排序。
     * <p>
     * 语义说明：本 Bean 仅在 aicyi.message.mq.enabled=true 时注册，属用户显式开启的能力；
     * 若此时 StreamBridge 缺失说明装配被人为排除，立即报错优于静默跳过（静默跳过会把
     * 配置失误伪装成"功能未开启"，难以排查）
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.mq",
            name = "provider",
            havingValue = "rabbitMq",
            matchIfMissing = true)
    public MqSender defaultMqSender(ObjectProvider<StreamBridge> streamBridgeProvider) {

        StreamBridge streamBridge = streamBridgeProvider.getIfAvailable();
        if (streamBridge == null) {
            throw new IllegalStateException(
                    "aicyi.message.mq.enabled=true and provider=rabbitMq, but no StreamBridge bean found. "
                            + "Check whether spring-cloud-stream auto-configuration (FunctionConfiguration) is excluded");
        }

        return new StreamMqSender(streamBridge);
    }
}
