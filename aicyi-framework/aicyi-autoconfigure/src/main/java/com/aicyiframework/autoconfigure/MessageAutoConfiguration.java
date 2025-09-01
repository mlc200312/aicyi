package com.aicyiframework.autoconfigure;

import com.aichuangyi.commons.core.message.*;
import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aicyiframework.integ.sms.TwilioSmsManager;
import com.aicyiframework.message.mail.*;
import com.aicyiframework.message.sms.SmsManager;
import com.aicyiframework.message.sms.SmsMessageSender;
import com.aicyiframework.message.stream.MqMessageSender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Optional;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:10
 **/
@EnableConfigurationProperties({SmsProperties.class, MailProperties.class})
public class MessageAutoConfiguration implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAutoConfiguration.class);

    private final MailProperties mailProperties;
    private final SmsProperties properties;

    public MessageAutoConfiguration(MailProperties mailProperties, SmsProperties properties) {
        this.mailProperties = mailProperties;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(EmailManager.class)
    public EmailManager emailManager() {
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost(mailProperties.getHost());
        emailConfig.setPort(mailProperties.getPort());
        emailConfig.setUsername(mailProperties.getUsername());
        emailConfig.setPassword(mailProperties.getPassword());
        emailConfig.setFromAddress(mailProperties.getUsername());
        emailConfig.setFromName(mailProperties.getProperties().get("fromName"));
        emailConfig.setCharset(mailProperties.getDefaultEncoding().displayName());
        EmailManager emailManager = new JavaMailEmailManager(emailConfig, new FreeMarkerTemplateEngine());
        return emailManager;
    }

    @Bean
    @ConditionalOnMissingBean(SmsManager.class)
    @ConditionalOnProperty(prefix = "aicyi.sms", name = "enabled", havingValue = "true")
    public SmsManager smsManager() {
        return new TwilioSmsManager(properties.getUsername(), properties.getPassword(), properties.getFrom(), new HashMap<>());
    }

    @Bean
    @ConditionalOnMissingBean(UnifiedMessageManager.class)
    public UnifiedMessageManager unifiedMessageManager(@Autowired(required = false) EmailManager emailManager, @Autowired(required = false) SmsManager smsManager) {
        MessageSenderFactory factory = new DefaultMessageSenderFactory();
        Optional.ofNullable(emailManager).ifPresent(item -> factory.registerSender(MessageType.EMAIL, new EmailMessageSender(emailManager)));
        Optional.ofNullable(smsManager).ifPresent(item -> factory.registerSender(MessageType.SMS, new SmsMessageSender(smsManager)));
        factory.registerSender(MessageType.MQ, new MqMessageSender());

        // 创建统一消息服务
        return new DefaultUnifiedMessageManager(factory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing Configuration '{}'!", this.getClass().getName());
    }
}
