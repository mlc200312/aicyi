package io.github.aicyi.midware.autoconfigure;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.message.*;
import io.github.aicyi.midware.message.mail.*;
import io.github.aicyi.midware.message.sms.DefaultSmsManager;
import io.github.aicyi.midware.message.sms.SmsManager;
import io.github.aicyi.midware.message.sms.SmsMessageSender;
import io.github.aicyi.midware.message.sms.SmsProperties;
import io.github.aicyi.midware.rabbitmq.MqManager;
import io.github.aicyi.midware.rabbitmq.MqMessageSender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * @author Mr.Min
 * @description Message配置自动注入
 * @date 18:10
 **/
@EnableConfigurationProperties({SmsProperties.class, MailProperties.class})
public class MessageAutoConfiguration implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAutoConfiguration.class);

    private final MailProperties mailProperties;

    public MessageAutoConfiguration(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    @Bean
    @ConditionalOnMissingBean(MailManager.class)
    public MailManager emailManager() {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setHost(mailProperties.getHost());
        mailConfig.setPort(mailProperties.getPort());
        mailConfig.setUsername(mailProperties.getUsername());
        mailConfig.setPassword(mailProperties.getPassword());
        mailConfig.setFromAddress(mailProperties.getUsername());
        mailConfig.setFromName(mailProperties.getProperties().get("fromName"));
        mailConfig.setCharset(mailProperties.getDefaultEncoding().displayName());
        MailManager mailManager = new DefaultMailManager(mailConfig, new FreeMarkerTemplateEngine());
        return mailManager;
    }

    @Bean
    @ConditionalOnMissingBean(SmsManager.class)
    @ConditionalOnBean(MailManager.class)
    @ConditionalOnProperty(prefix = "aicyi.sms", name = "enabled", havingValue = "true")
    public SmsManager smsManager(MailManager mailManager) {
        return new DefaultSmsManager(mailManager);
    }

    @Bean
    @ConditionalOnMissingBean(UnifiedMessageManager.class)
    public UnifiedMessageManager unifiedMessageManager(@Autowired(required = false) MailManager mailManager,
                                                       @Autowired(required = false) SmsManager smsManager,
                                                       @Autowired(required = false) MqManager mqManager) {
        MessageSenderFactory factory = new DefaultMessageSenderFactory();
        Optional.ofNullable(mailManager).ifPresent(item -> factory.registerSender(MessageType.EMAIL, new MailMessageSender(mailManager)));
        Optional.ofNullable(smsManager).ifPresent(item -> factory.registerSender(MessageType.SMS, new SmsMessageSender(smsManager)));
        Optional.ofNullable(mqManager).ifPresent(item -> factory.registerSender(MessageType.MQ, new MqMessageSender(mqManager)));

        // 创建统一消息服务
        return new DefaultUnifiedMessageManager(factory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing Configuration '{}'!", this.getClass().getName());
    }
}
