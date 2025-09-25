package io.github.aicyi.midware.rabbitmq;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 消息发送接口
 * @date 2025/9/10
 **/
public interface MessageSender {
    /**
     * 发送消息
     *
     * @param destination 目标（队列/主题）
     * @param message     消息内容
     * @return 发送结果
     */
    boolean send(String destination, Object message);

    /**
     * 发送带属性的消息
     *
     * @param destination 目标
     * @param message     消息内容
     * @param properties  消息属性
     * @return 发送结果
     */
    boolean send(String destination, Object message, Map<String, Object> properties);

    /**
     * 发送延迟消息
     *
     * @param destination 目标
     * @param message     消息内容
     * @param delay       延迟时间（毫秒）
     * @return 发送结果
     */
    boolean sendDelayed(String destination, Object message, long delay);

    /**
     * 批量发送消息
     *
     * @param destination 目标
     * @param messages    消息列表
     * @return 发送结果列表
     */
    List<Boolean> sendBatch(String destination, List<?> messages);
}