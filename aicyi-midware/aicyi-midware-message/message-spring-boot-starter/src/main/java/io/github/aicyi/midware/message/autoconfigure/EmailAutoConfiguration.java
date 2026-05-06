package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.midware.message.mail.sender.DefaultEmailSender;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.mail.template.FreeMarkerTemplateEngine;
import io.github.aicyi.midware.message.mail.model.MailConfig;
import io.github.aicyi.midware.message.properties.MessageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(
        prefix = "aicyi.message.email",
        name = "enabled",
        havingValue = "true")
public class EmailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EmailSender defaultEmailSender(MessageProperties messageProperties) {
        MessageProperties.EmailProperties emailProperties = messageProperties.getEmail();
        MailConfig mailConfig = new MailConfig();
        mailConfig.setHost(emailProperties.getHost());
        mailConfig.setPort(emailProperties.getPort());
        mailConfig.setUsername(emailProperties.getUsername());
        mailConfig.setPassword(emailProperties.getPassword());
        mailConfig.setFromAddress(emailProperties.getUsername());
        mailConfig.setFromName(emailProperties.getFromName());
        return new DefaultEmailSender(mailConfig, new FreeMarkerTemplateEngine());
    }
}