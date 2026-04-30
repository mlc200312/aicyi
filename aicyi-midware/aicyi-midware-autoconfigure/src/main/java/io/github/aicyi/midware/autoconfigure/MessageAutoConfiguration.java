package io.github.aicyi.midware.autoconfigure;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.message.*;
import io.github.aicyi.midware.message.mail.*;
import io.github.aicyi.midware.message.sms.*;
import io.github.aicyi.midware.message.sms.DefaultSmsSender;
import io.github.aicyi.midware.message.sms.SmsSender;
import io.github.aicyi.midware.message.mq.MqSender;
import io.github.aicyi.midware.message.mq.MqMessageSender;
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
    @ConditionalOnMissingBean(MailSender.class)
    public MailSender emailManager() {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setHost(mailProperties.getHost());
        mailConfig.setPort(mailProperties.getPort());
        mailConfig.setUsername(mailProperties.getUsername());
        mailConfig.setPassword(mailProperties.getPassword());
        mailConfig.setFromAddress(mailProperties.getUsername());
        mailConfig.setFromName(mailProperties.getProperties().get("fromName"));
        mailConfig.setCharset(mailProperties.getDefaultEncoding().displayName());
        MailSender mailSender = new JavaMailSender(mailConfig, new FreeMarkerTemplateEngine());
        return mailSender;
    }

    @Bean
    @ConditionalOnMissingBean(SmsSender.class)
    @ConditionalOnBean(MailSender.class)
    @ConditionalOnProperty(prefix = "aicyi.sms", name = "enabled", havingValue = "true")
    public SmsSender smsManager(MailSender mailSender) {
        return new DefaultSmsSender(mailSender);
    }

    @Bean
    @ConditionalOnMissingBean(UnifiedMessageManager.class)
    public UnifiedMessageManager unifiedMessageManager(@Autowired(required = false) MailSender mailSender,
                                                       @Autowired(required = false) SmsSender smsSender,
                                                       @Autowired(required = false) MqSender mqSender) {
        MessageSenderFactory factory = new DefaultMessageSenderFactory();
        Optional.ofNullable(mailSender).ifPresent(item -> factory.registerSender(MessageType.MAIL, new MailMessageSender(mailSender)));
        Optional.ofNullable(smsSender).ifPresent(item -> factory.registerSender(MessageType.SMS, new SmsMessageSender(smsSender)));
        Optional.ofNullable(mqSender).ifPresent(item -> factory.registerSender(MessageType.MQ, new MqMessageSender(mqSender)));

        // 创建统一消息服务
        return new DefaultUnifiedMessageManager(factory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing Configuration '{}'!", this.getClass().getName());
    }
}
