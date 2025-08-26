package com.aichuangyi.demo.config;

import com.aicyiframework.core.mail.EmailConfig;
import com.aicyiframework.core.mail.EmailManager;
import com.aicyiframework.core.mail.FreeMarkerTemplateEngine;
import com.aicyiframework.core.mail.JavaMailEmailManager;
import com.aicyiframework.core.message.*;
import com.aicyiframework.core.sms.SmsManager;
import com.aicyiframework.integ.sms.EmailToSmsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:43
 **/
@Configuration
public class MessageConfig {

    @Autowired
    private MailProperties mailProperties;

    @Bean
    public EmailManager emailManager() {
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost(mailProperties.getHost());
        emailConfig.setPort(mailProperties.getPort());
        emailConfig.setUsername(mailProperties.getUsername());
        emailConfig.setPassword(mailProperties.getPassword());
        emailConfig.setFromAddress("minlc1024@163.com");
        emailConfig.setFromName("minliangchao");
        emailConfig.setCharset("UTF-8");
        JavaMailEmailManager javaMailEmailManager = new JavaMailEmailManager(emailConfig, new FreeMarkerTemplateEngine());
        return javaMailEmailManager;
    }

    @Bean
    public SmsManager smsManager() {
        EmailToSmsManager smsManager = new EmailToSmsManager();
        smsManager.setEmailManager(emailManager());
        return smsManager;
    }

    @Bean
    public UnifiedMessageManager unifiedMessageManager() {
        MessageSenderFactory factory = new DefaultMessageSenderFactory();
        factory.registerSender(MessageType.EMAIL, new EmailMessageSender(emailManager()));
        factory.registerSender(MessageType.SMS, new SmsMessageSender(smsManager()));
        factory.registerSender(MessageType.MQ, new MqMessageSender());

        // 创建统一消息服务
        return new DefaultUnifiedMessageManager(factory);
    }
}
