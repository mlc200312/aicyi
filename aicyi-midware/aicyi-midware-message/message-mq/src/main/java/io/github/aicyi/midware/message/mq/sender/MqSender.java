package io.github.aicyi.midware.message.mq.sender;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 消息发送接口
 * @date 2025/9/10
 **/
public interface MqSender {
    /**
     * 发送消息
     *
     * @param channel 目标（队列/主题）
     * @param payload 消息内容
     * @return 发送结果
     */
    boolean send(String channel, Object payload);

    /**
     * 发送带属性的消息
     *
     * @param channel 目标
     * @param payload 消息内容
     * @param headers 消息属性
     * @return 发送结果
     */
    boolean send(String channel, Object payload, Map<String, Object> headers);

    /**
     * 发送延迟消息
     *
     * @param channel     目标
     * @param payload     消息内容
     * @param delayMillis 延迟时间（毫秒）
     * @return 发送结果
     */
    boolean sendDelayed(String channel, Object payload, long delayMillis);

    /**
     * 批量发送消息
     *
     * @param channel  目标
     * @param payloads 消息列表
     * @return 发送结果列表
     */
    List<Boolean> sendBatch(String channel, List<?> payloads);
}