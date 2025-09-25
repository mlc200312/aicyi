package io.github.aicyi.midware.rabbitmq;

import io.github.aicyi.commons.core.message.AbstractMessage;
import io.github.aicyi.commons.core.message.MessageType;

/**
 * @author Mr.Min
 * @description MQ消息
 * @date 2025/8/25
 **/
public class MqMessage extends AbstractMessage<Object> {
    private final String destination; // 消息主题
    private final String group; // 组
    private final String routingKey; // 路由键
    private final Long delay; // 延迟时间（毫秒）

    // 私有构造函数，只能通过Builder创建
    private MqMessage(Builder builder) {
        super(builder.content, MessageType.MQ);
        this.destination = builder.destination;
        this.group = builder.group;
        this.routingKey = builder.routingKey;
        this.delay = builder.delay;
    }

    // Getter 方法
    public String getDestination() {
        return destination;
    }

    public String getGroup() {
        return group;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public Long getDelay() {
        return delay;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private Object content;
        private String destination;
        private String group;
        private String routingKey;
        private Long delay;

        public Builder() {
            // 空构造
        }

        public Builder content(Object content) {
            this.content = content;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder group(String group) {
            this.group = group;
            return this;
        }

        public Builder routingKey(String routingKey) {
            this.routingKey = routingKey;
            return this;
        }

        public Builder delayLevel(Long delay) {
            this.delay = delay;
            return this;
        }

        /**
         * 构建 MqMessage 实例
         *
         * @throws IllegalArgumentException 如果必需字段缺失
         */
        public MqMessage build() {
            // 验证必需字段
            if (content == null) {
                throw new IllegalArgumentException("消息内容不能为空");
            }
            if (destination == null || destination.trim().isEmpty()) {
                throw new IllegalArgumentException("消息主题不能为空");
            }

            return new MqMessage(this);
        }

        /**
         * 快速构建方法（跳过验证，用于某些特殊场景）
         */
        public MqMessage buildUnsafe() {
            return new MqMessage(this);
        }
    }

    /**
     * 静态工厂方法，创建Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 快速创建方法
     */
    public static MqMessage of(Object content, String destination) {
        return builder()
                .content(content)
                .destination(destination)
                .build();
    }

    /**
     * 快速创建方法（带标签）
     */
    public static MqMessage of(Object content, String destination, String tag) {
        return builder()
                .content(content)
                .destination(destination)
                .group(tag)
                .build();
    }

    /**
     * 快速创建方法（带标签和业务routingKey）
     */
    public static MqMessage of(Object content, String destination, String tag, String routingKey) {
        return builder()
                .content(content)
                .destination(destination)
                .group(tag)
                .routingKey(routingKey)
                .build();
    }

    /**
     * 验证消息基本信息是否完整
     */
    public boolean isValid() {
        return getContent() != null &&
                destination != null && !destination.trim().isEmpty();
    }

    /**
     * 是否延迟消息
     */
    public boolean isDelayed() {
        return delay != null && delay > 0;
    }

    /**
     * 是否包含业务routingKey
     */
    public boolean hasRoutingKey() {
        return routingKey != null && !routingKey.trim().isEmpty();
    }

    /**
     * 是否包含标签
     */
    public boolean hasGroup() {
        return group != null && !group.trim().isEmpty();
    }
}