package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.midware.message.core.model.TemplateEngineType;
import io.github.aicyi.midware.message.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.sms.template.DefaultSmsTemplateEngine;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.sms.sender.TwilioSmsSender;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.sms.sender.DefaultSmsSender;
import io.github.aicyi.midware.message.sms.sender.SmsSender;
import io.github.aicyi.midware.message.sms.sender.YunPianSmsSender;
import io.github.aicyi.midware.message.template.factory.DefaultTemplateEngineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "aicyi.message.sms",
        name = "enabled",
        havingValue = "true")
public class SmsAutoConfiguration {

    private final EmailSender emailSender;

    private final TemplateProvider templateProvider;

    public SmsAutoConfiguration(@Autowired(required = false) EmailSender emailSender,
                                @Autowired(required = false) TemplateProvider templateProvider) {
        this.emailSender = emailSender;
        this.templateProvider = templateProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "default")
    public SmsSender defaultSmsSender() {

        TemplateEngineFactory factory = new DefaultTemplateEngineFactory();

        factory.register(TemplateEngineType.SIMPLE, new DefaultSmsTemplateEngine());

        return new DefaultSmsSender(templateProvider, factory, emailSender);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "twilio")
    public SmsSender twilioSmsSender(MessageProperties messageProperties) {
        MessageProperties.SmsProperties smsProperties = messageProperties.getSms();

        TemplateEngineFactory factory = new DefaultTemplateEngineFactory();

        factory.register(TemplateEngineType.SIMPLE, new DefaultSmsTemplateEngine());

        return new TwilioSmsSender(smsProperties.getUsername(), smsProperties.getPassword(), smsProperties.getFrom(), templateProvider, factory);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "yunPian")
    public SmsSender yunPianSmsSender(MessageProperties messageProperties) {

        MessageProperties.SmsProperties smsProperties = messageProperties.getSms();

        return new YunPianSmsSender(smsProperties.getUsername());
    }
}