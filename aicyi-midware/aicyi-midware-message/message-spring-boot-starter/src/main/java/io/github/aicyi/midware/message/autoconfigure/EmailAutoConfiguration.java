package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.mail.sender.DefaultJavaEmailSender;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.mail.model.MailConfig;
import io.github.aicyi.midware.message.mail.template.DefualtMailTemplateEngine;
import io.github.aicyi.midware.message.mail.template.FreeMarkerTemplateEngine;
import io.github.aicyi.midware.message.mail.template.ThymeleafTemplateEngine;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.template.factory.DefaultTemplateEngineFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "aicyi.message.email",
        name = "enabled",
        havingValue = "true")
public class EmailAutoConfiguration {

    private final TemplateProvider templateProvider;

    public EmailAutoConfiguration(@Autowired(required = false) TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailSender defaultEmailSender(MessageProperties messageProperties) {
        // 创建邮件发送者
        MailConfig mailConfig = getMailConfig(messageProperties);

        // 创建模板引擎工厂
        TemplateEngineFactory factory = getTemplateEngineFactory();

        return new DefaultJavaEmailSender(templateProvider, factory, mailConfig);
    }

    // 创建模板引擎
    private static @NotNull TemplateEngineFactory getTemplateEngineFactory() {
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

        TemplateEngineFactory factory = new DefaultTemplateEngineFactory();
        factory.register(TemplateEngineType.SIMPLE, new DefualtMailTemplateEngine());
        factory.register(TemplateEngineType.THYMELEAF, thymeleafTemplateEngine);
        factory.register(TemplateEngineType.FREEMARKER, freeMarkerTemplateEngine);
        return factory;
    }

    // 获取邮件配置
    private static @NotNull MailConfig getMailConfig(MessageProperties messageProperties) {
        MessageProperties.EmailProperties emailProperties = messageProperties.getEmail();
        MailConfig mailConfig = MailConfig.builder()
                .host(emailProperties.getHost())
                .port(emailProperties.getPort())
                .username(emailProperties.getUsername())
                .password(emailProperties.getPassword())
                .fromAddress(emailProperties.getUsername())
                .fromName(emailProperties.getFromName())
                .build();
        return mailConfig;
    }
}