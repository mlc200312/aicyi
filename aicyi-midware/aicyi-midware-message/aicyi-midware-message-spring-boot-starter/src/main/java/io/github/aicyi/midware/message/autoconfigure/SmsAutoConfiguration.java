package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.sms.adapter.SmsMessageSender;
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
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(after = EmailAutoConfiguration.class)
@ConditionalOnClass(SmsSender.class)
@ConditionalOnProperty(
        prefix = "aicyi.message.sms",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SmsAutoConfiguration {

    private final TemplateProvider templateProvider;

    public SmsAutoConfiguration(@Autowired(required = false) TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
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

    /**
     * 短信渠道适配器：包装 {@link SmsSender} 接入统一消息框架。
     * 由本渠道配置自行装配，避免 MessageAutoConfiguration 直接引用渠道类
     */
    @Bean
    @ConditionalOnBean(SmsSender.class)
    public MessageSender smsMessageSender(SmsSender smsSender) {
        return new SmsMessageSender(smsSender);
    }

    /**
     * default 短信实现（邮件网关转短信）依赖邮件模块的 {@link EmailSender}：
     * 独立为嵌套配置并以类守卫隔离，未引入 mail 模块时跳过装配，
     * 避免构造器/参数引用缺失类导致 NoClassDefFoundError
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(EmailSender.class)
    static class DefaultSmsSenderConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(EmailSender.class)
        @ConditionalOnProperty(
                prefix = "aicyi.message.sms",
                name = "provider",
                havingValue = "default")
        public SmsSender defaultSmsSender(@Autowired(required = false) TemplateProvider templateProvider,
                                          TemplateEngineFactory templateEngineFactory,
                                          EmailSender emailSender) {
            return new DefaultSmsSender(templateProvider, templateEngineFactory, emailSender);
        }
    }
}
