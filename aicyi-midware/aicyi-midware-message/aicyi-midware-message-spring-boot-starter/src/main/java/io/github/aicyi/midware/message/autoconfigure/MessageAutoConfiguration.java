package io.github.aicyi.midware.message.autoconfigure;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.message.MessageSender;
import io.github.aicyi.commons.core.message.MessageType;
import io.github.aicyi.commons.core.template.DefaultTemplateEngine;
import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.commons.core.template.TemplateEngineType;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.midware.message.core.sender.ChannelMessageSender;
import io.github.aicyi.midware.message.core.sender.DefaultUnifiedMessageManager;
import io.github.aicyi.midware.message.core.sender.MessageSenderFactory;
import io.github.aicyi.midware.message.core.sender.UnifiedMessageManager;
import io.github.aicyi.midware.message.factory.DefaultMessageSenderFactory;
import io.github.aicyi.midware.message.properties.MessageProperties;
import io.github.aicyi.midware.message.template.factory.DefaultTemplateEngineFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;

/**
 * @author Mr.Min
 * @description Message配置自动注入
 * <p>
 * 本配置类不引用任何渠道模块（mail/sms/mq/rabbitmq）的类：各渠道适配器由对应渠道
 * AutoConfiguration 按自身类守卫装配为 {@link MessageSender} Bean（实现
 * {@link ChannelMessageSender} 标记），本类仅通过 {@link MessageSender} 接口聚合，
 * 只引入 starter 未引入任何渠道模块时不会触发 NoClassDefFoundError
 * @date 18:10
 **/
// 与各渠道 AutoConfiguration 之间不声明相互排序：渠道适配器作为 MessageSender Bean
// 在 unifiedMessageManager 实例化期注入，与装配顺序无关（注册顺序敏感性已由
// ChannelMessageSender 标记两遍注册解耦：内置渠道先注册、业务覆盖后注册）。
// 声明双向 before 排序会触发 Spring Boot AutoConfigurationSorter 的环检测
@AutoConfiguration
@EnableConfigurationProperties({MessageProperties.class})
public class MessageAutoConfiguration implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    @ConditionalOnMissingBean
    public TemplateEngineFactory templateEngineFactory() {
        // 共享模板引擎工厂：默认注册 SIMPLE 引擎，业务可通过自定义 Bean 整体覆盖
        TemplateEngineFactory factory = new DefaultTemplateEngineFactory();
        factory.register(TemplateEngineType.SIMPLE, new DefaultTemplateEngine());
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(UnifiedMessageManager.class)
    public UnifiedMessageManager unifiedMessageManager(ObjectProvider<List<MessageSender>> messageSenders) {
        MessageSenderFactory factory = new DefaultMessageSenderFactory();

        List<MessageSender> senders = messageSenders.getIfAvailable(Collections::emptyList);

        // 先注册基础包内置渠道适配器，再注册业务自定义发送器；
        // 同一消息类型后注册覆盖先注册，保证业务 Bean 可覆盖默认渠道实现
        for (MessageSender sender : senders) {
            if (sender instanceof ChannelMessageSender) {
                registerSender(factory, sender);
            }
        }
        for (MessageSender sender : senders) {
            if (!(sender instanceof ChannelMessageSender)) {
                registerSender(factory, sender);
            }
        }

        // 创建统一消息服务
        return new DefaultUnifiedMessageManager(factory);
    }

    private static void registerSender(MessageSenderFactory factory, MessageSender sender) {
        for (MessageType messageType : MessageType.values()) {
            if (sender.supports(messageType)) {
                factory.registerSender(messageType, sender);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Initializing Configuration '{}'!", this.getClass().getName());
    }
}
