package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.commons.core.message.MessageSender;

/**
 * @author Mr.Min
 * @description 基础包内置渠道消息发送器标记接口
 * <p>
 * 由基础包各渠道适配器（邮件/短信/MQ）实现，用于在统一装配时与业务自定义
 * {@link MessageSender} 区分：内置渠道发送器先注册，业务自定义发送器后注册，
 * 同一消息类型后注册覆盖先注册，保证业务 Bean 可覆盖默认渠道实现。
 * @date 2026/8/24
 **/
public interface ChannelMessageSender extends MessageSender {
}
