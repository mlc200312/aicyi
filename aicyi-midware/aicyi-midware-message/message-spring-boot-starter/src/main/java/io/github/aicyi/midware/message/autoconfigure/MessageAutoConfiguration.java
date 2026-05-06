package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.midware.message.core.model.MessageType;
import io.github.aicyi.midware.message.core.sender.DefaultUnifiedMessageManager;
import io.github.aicyi.midware.message.core.sender.MessageSenderFactory;
import io.github.aicyi.midware.message.core.sender.UnifiedMessageManager;
import io.github.aicyi.midware.message.factory.DefaultMessageSenderFactory;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.mail.sender.EmailMessageSender;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.sms.sender.SmsSender;
import io.github.aicyi.midware.message.mq.sender.MqSender;
import io.github.aicyi.midware.message.mq.sender.MqMessageSender;
import io.github.aicyi.midware.message.sms.sender.SmsMessageSender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * @author Mr.Min
 * @description Message配置自动注入
 * @date 18:10
 **/
@EnableConfigurationProperties({MessageProperties.class})
public class MessageAutoConfiguration implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAutoConfiguration.class);

    private final EmailSender defaultEmailSender;
    private final MqSender defaultMqSender;
    private final SmsSender defaultSmsSender;

    public MessageAutoConfiguration(@Autowired(required = false) EmailSender defaultEmailSender,
                                    @Autowired(required = false) MqSender defaultMqSender,
                                    @Autowired(required = false) SmsSender defaultSmsSender) {
        this.defaultEmailSender = defaultEmailSender;
        this.defaultMqSender = defaultMqSender;
        this.defaultSmsSender = defaultSmsSender;
    }

    @Bean
    @ConditionalOnMissingBean(UnifiedMessageManager.class)
    public UnifiedMessageManager unifiedMessageManager() {
        MessageSenderFactory factory = new DefaultMessageSenderFactory();
        Optional.ofNullable(defaultEmailSender).ifPresent(item -> factory.registerSender(MessageType.MAIL, new EmailMessageSender(item)));
        Optional.ofNullable(defaultMqSender).ifPresent(item -> factory.registerSender(MessageType.MQ, new MqMessageSender(item)));
        Optional.ofNullable(defaultSmsSender).ifPresent(item -> factory.registerSender(MessageType.SMS, new SmsMessageSender(item)));

        // 创建统一消息服务
        return new DefaultUnifiedMessageManager(factory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing Configuration '{}'!", this.getClass().getName());
    }
}
