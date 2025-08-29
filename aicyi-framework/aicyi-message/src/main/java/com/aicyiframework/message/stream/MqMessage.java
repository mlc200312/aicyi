package com.aicyiframework.message.stream;

import com.aicyiframework.core.message.AbstractMessage;
import com.aicyiframework.core.message.MessageType;

/**
 * @author Mr.Min
 * @description MQ消息
 * @date 2025/8/25
 **/
public class MqMessage extends AbstractMessage<Object> {
    private final String topic; // 消息主题
    private final String tag; // 消息标签
    private final String key; // 业务key
    private final Integer delayLevel; // 延迟级别

    // 私有构造函数，只能通过Builder创建
    private MqMessage(Builder builder) {
        super(builder.content, MessageType.MQ);
        this.topic = builder.topic;
        this.tag = builder.tag;
        this.key = builder.key;
        this.delayLevel = builder.delayLevel;
    }

    // Getter 方法
    public String getTopic() {
        return topic;
    }

    public String getTag() {
        return tag;
    }

    public String getKey() {
        return key;
    }

    public Integer getDelayLevel() {
        return delayLevel;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private Object content;
        private String topic;
        private String tag;
        private String key;
        private Integer delayLevel;

        public Builder() {
            // 空构造
        }

        public Builder content(Object content) {
            this.content = content;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder delayLevel(Integer delayLevel) {
            this.delayLevel = delayLevel;
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
            if (topic == null || topic.trim().isEmpty()) {
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
    public static MqMessage of(Object content, String topic) {
        return builder()
                .content(content)
                .topic(topic)
                .build();
    }

    /**
     * 快速创建方法（带标签）
     */
    public static MqMessage of(Object content, String topic, String tag) {
        return builder()
                .content(content)
                .topic(topic)
                .tag(tag)
                .build();
    }

    /**
     * 快速创建方法（带标签和业务key）
     */
    public static MqMessage of(Object content, String topic, String tag, String key) {
        return builder()
                .content(content)
                .topic(topic)
                .tag(tag)
                .key(key)
                .build();
    }

    /**
     * 验证消息基本信息是否完整
     */
    public boolean isValid() {
        return getContent() != null &&
                topic != null && !topic.trim().isEmpty();
    }

    /**
     * 是否延迟消息
     */
    public boolean isDelayed() {
        return delayLevel != null && delayLevel > 0;
    }

    /**
     * 是否包含业务key
     */
    public boolean hasKey() {
        return key != null && !key.trim().isEmpty();
    }

    /**
     * 是否包含标签
     */
    public boolean hasTag() {
        return tag != null && !tag.trim().isEmpty();
    }
}