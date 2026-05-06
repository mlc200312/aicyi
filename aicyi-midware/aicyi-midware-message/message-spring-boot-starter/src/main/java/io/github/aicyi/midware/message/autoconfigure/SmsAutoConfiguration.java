package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.sms.sender.TwilioSmsSender;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.sms.sender.DefaultSmsSender;
import io.github.aicyi.midware.message.sms.sender.SmsSender;
import io.github.aicyi.midware.message.sms.sender.YunPianSmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(
        prefix = "aicyi.message.sms",
        name = "enabled",
        havingValue = "true")
public class SmsAutoConfiguration {

    private final EmailSender emailSender;

    public SmsAutoConfiguration(@Autowired(required = false) EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "default")
    @ConditionalOnBean(EmailSender.class)
    public SmsSender defaultSmsSender() {
        return new DefaultSmsSender(emailSender);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "aicyi.message.sms",
            name = "provider",
            havingValue = "twilio")
    public SmsSender twilioSmsSender(MessageProperties messageProperties) {
        MessageProperties.SmsProperties smsProperties = messageProperties.getSms();
        return new TwilioSmsSender(smsProperties.getUsername(), smsProperties.getPassword(), smsProperties.getFrom());
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