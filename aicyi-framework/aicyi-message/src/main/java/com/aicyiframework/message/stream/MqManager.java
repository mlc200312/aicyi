package com.aicyiframework.message.stream;


import com.aichuangyi.commons.core.message.SendResult;

/**
 * @author Mr.Min
 * @description MQ消息服务
 * @date 2025/8/25
 **/
public interface MqManager {

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @return 发送结果
     */
    <T> SendResult send(T message);

    /**
     * 发送消息到指定路由
     *
     * @param message    消息内容
     * @param routingKey 路由键
     * @return 发送结果
     */
    <T> SendResult send(T message, String routingKey);

    /**
     * 发送消息到指定交换机和路由
     *
     * @param message    消息内容
     * @param exchange   交换机名称
     * @param routingKey 路由键
     * @return 发送结果
     */
    <T> SendResult send(T message, String exchange, String routingKey);

    /**
     * 发送延迟消息
     *
     * @param message     消息内容
     * @param delayMillis 延迟时间(毫秒)
     * @return 发送结果
     */
    <T> SendResult sendDelayed(T message, long delayMillis);

    /**
     * 发送延迟消息到指定路由
     *
     * @param message     消息内容
     * @param routingKey  路由键
     * @param delayMillis 延迟时间(毫秒)
     * @return 发送结果
     */
    <T> SendResult sendDelayed(T message, String routingKey, long delayMillis);
}
