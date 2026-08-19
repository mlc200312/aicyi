package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.core.message.MessageType;
import io.github.aicyi.commons.core.template.DefaultTemplateEngine;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.midware.message.core.sender.DefaultUnifiedMessageManager;
import io.github.aicyi.midware.message.core.sender.MessageSenderFactory;
import io.github.aicyi.midware.message.core.sender.UnifiedMessageManager;
import io.github.aicyi.midware.message.factory.DefaultMessageSenderFactory;
import io.github.aicyi.midware.message.mail.adapter.EmailMessageSender;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.mq.adapter.MqMessageSender;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.sms.adapter.SmsMessageSender;
import io.github.aicyi.midware.message.sms.sender.SmsSender;
import io.github.aicyi.midware.message.mq.sender.MqSender;
import io.github.aicyi.midware.message.template.factory.DefaultTemplateEngineFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Mr.Min
 * @description Message配置自动注入
 * <p>
 * 渠道发送器统一走 Bean 方法参数 + ObjectProvider 注入（而非配置类构造器），
 * 避免与各渠道 AutoConfiguration 的工厂方法 Bean 形成循环依赖
 * @date 18:10
 **/
@AutoConfiguration
@EnableConfigurationProperties({MessageProperties.class})
public class MessageAutoConfiguration implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    @ConditionalOnMissingBean
    public TemplateEngineFactory templateEngineFactory() {
        // 共享模板引擎工厂：默认注册 SIMPLE 引擎，业务可通过自定义 Bean 整体覆盖
        TemplateEngineFactory factory = new DefaultTemplateEngineFactory();
        factory.register(TemplateEngineType.SIMPLE, new DefaultTemplateEngine());
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(UnifiedMessageManager.class)
    public UnifiedMessageManager unifiedMessageManager(
            ObjectProvider<EmailSender> emailSenderProvider,
            ObjectProvider<MqSender> mqSenderProvider,
            ObjectProvider<SmsSender> smsSenderProvider,
            ObjectProvider<List<MessageSender>> messageSenders) {
        MessageSenderFactory factory = new DefaultMessageSenderFactory();
        Optional.ofNullable(emailSenderProvider.getIfAvailable()).ifPresent(item -> factory.registerSender(MessageType.MAIL, new EmailMessageSender(item)));
        Optional.ofNullable(mqSenderProvider.getIfAvailable()).ifPresent(item -> factory.registerSender(MessageType.MQ, new MqMessageSender(item)));
        Optional.ofNullable(smsSenderProvider.getIfAvailable()).ifPresent(item -> factory.registerSender(MessageType.SMS, new SmsMessageSender(item)));

        // 业务自定义发送器后注册，同类型覆盖默认渠道实现（允许业务覆盖 Bean）
        for (MessageSender sender : messageSenders.getIfAvailable(Collections::emptyList)) {
            for (MessageType messageType : MessageType.values()) {
                if (sender.supports(messageType)) {
                    factory.registerSender(messageType, sender);
                }
            }
        }

        // 创建统一消息服务
        return new DefaultUnifiedMessageManager(factory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Initializing Configuration '{}'!", this.getClass().getName());
    }
}
