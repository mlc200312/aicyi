package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.mail.adapter.EmailMessageSender;
import io.github.aicyi.midware.message.mail.config.MailConfig;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.mail.sender.impl.JavaMailEmailSender;
import io.github.aicyi.midware.message.template.engine.FreeMarkerTemplateEngine;
import io.github.aicyi.midware.message.template.engine.ThymeleafTemplateEngine;
import io.github.aicyi.midware.message.properties.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@AutoConfiguration
@ConditionalOnClass(EmailSender.class)
@ConditionalOnProperty(
        prefix = "aicyi.message.email",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class EmailAutoConfiguration {

    private final TemplateProvider templateProvider;

    public EmailAutoConfiguration(@Autowired(required = false) TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailSender defaultEmailSender(MessageProperties messageProperties, TemplateEngineFactory templateEngineFactory) {
        // 创建邮件发送者
        MailConfig mailConfig = getMailConfig(messageProperties);

        // 在共享模板引擎工厂上补充邮件渠道所需引擎
        registerTemplateEngines(templateEngineFactory);

        return new JavaMailEmailSender(templateProvider, templateEngineFactory, mailConfig);
    }

    /**
     * 邮件渠道适配器：包装 {@link EmailSender}（业务覆盖的 Bean 同样生效）接入统一消息框架。
     * 由本渠道配置自行装配，避免 MessageAutoConfiguration 直接引用渠道类
     */
    @Bean
    @ConditionalOnBean(EmailSender.class)
    public MessageSender emailMessageSender(EmailSender emailSender) {
        return new EmailMessageSender(emailSender);
    }

    // 注册邮件渠道模板引擎（Thymeleaf / FreeMarker）
    private static void registerTemplateEngines(TemplateEngineFactory factory) {
        org.thymeleaf.TemplateEngine templateEngine = new org.thymeleaf.TemplateEngine();
        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        stringTemplateResolver.setCacheable(true);
        stringTemplateResolver.setTemplateMode(TemplateMode.HTML);

        templateEngine.setDialect(new StandardDialect());
        templateEngine.setTemplateResolver(stringTemplateResolver);

        // 创建Thymeleaf模板引擎
        ThymeleafTemplateEngine thymeleafTemplateEngine = new ThymeleafTemplateEngine(templateEngine);

        // 创建FreeMarker模板引擎
        FreeMarkerTemplateEngine freeMarkerTemplateEngine = new FreeMarkerTemplateEngine();

        factory.register(TemplateEngineType.THYMELEAF, thymeleafTemplateEngine);
        factory.register(TemplateEngineType.FREEMARKER, freeMarkerTemplateEngine);
    }

    // 获取邮件配置
    private static MailConfig getMailConfig(MessageProperties messageProperties) {
        MessageProperties.EmailProperties emailProperties = messageProperties.getEmail();
        return MailConfig.builder()
                .host(emailProperties.getHost())
                .port(emailProperties.getPort())
                .username(emailProperties.getUsername())
                .password(emailProperties.getPassword())
                .fromAddress(emailProperties.getUsername())
                .fromName(emailProperties.getFromName())
                .build();
    }
}
