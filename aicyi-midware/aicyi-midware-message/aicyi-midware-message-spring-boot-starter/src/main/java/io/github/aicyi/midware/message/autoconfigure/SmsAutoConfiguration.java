package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.sms.sender.SmsSender;
import io.github.aicyi.midware.message.sms.sender.impl.DefaultSmsSender;
import io.github.aicyi.midware.message.sms.sender.impl.TwilioSmsSender;
import io.github.aicyi.midware.message.sms.sender.impl.YunPianSmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = EmailAutoConfiguration.class)
@ConditionalOnClass(SmsSender.class)
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
    @ConditionalOnBean(EmailSender.class)
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "default")
    public SmsSender defaultSmsSender(TemplateEngineFactory templateEngineFactory) {
        // default 实现为邮件网关转短信，必须存在 EmailSender（由 EmailAutoConfiguration 提供）
        return new DefaultSmsSender(templateProvider, templateEngineFactory, emailSender);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "twilio")
    public SmsSender twilioSmsSender(MessageProperties messageProperties, TemplateEngineFactory templateEngineFactory) {
        MessageProperties.SmsProperties smsProperties = messageProperties.getSms();

        return new TwilioSmsSender(smsProperties.getUsername(), smsProperties.getPassword(), smsProperties.getFrom(), templateProvider, templateEngineFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "yunPian")
    public SmsSender yunPianSmsSender(MessageProperties messageProperties, TemplateEngineFactory templateEngineFactory) {

        MessageProperties.SmsProperties smsProperties = messageProperties.getSms();

        return new YunPianSmsSender(smsProperties.getUsername(), templateProvider, templateEngineFactory);
    }
}
