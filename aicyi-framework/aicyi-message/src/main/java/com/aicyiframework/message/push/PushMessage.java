package com.aicyiframework.message.push;

import com.aichuangyi.commons.core.message.AbstractMessage;
import com.aichuangyi.commons.core.message.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Min
 * @description APP推送消息
 * @date 2025/8/25
 **/
public class PushMessage extends AbstractMessage<String> {
    private final List<String> deviceTokens; // 设备token列表
    private final String title; // 推送标题
    private final PushPlatform platform; // 推送平台(ALL/IOS/ANDROID)

    // 私有构造函数，只能通过Builder创建
    private PushMessage(Builder builder) {
        super(builder.content, MessageType.PUSH);
        this.deviceTokens = builder.deviceTokens;
        this.title = builder.title;
        this.platform = builder.platform;
    }

    // Getter 方法
    public List<String> getDeviceTokens() {
        return deviceTokens;
    }

    public String getTitle() {
        return title;
    }

    public PushPlatform getPlatform() {
        return platform;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private String content;
        private List<String> deviceTokens = new ArrayList<>();
        private String title;
        private PushPlatform platform = PushPlatform.ALL; // 默认所有平台

        public Builder() {
            // 空构造
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder deviceToken(String deviceToken) {
            if (deviceToken != null && !deviceToken.trim().isEmpty()) {
                this.deviceTokens.add(deviceToken.trim());
            }
            return this;
        }

        public Builder deviceTokens(List<String> deviceTokens) {
            if (deviceTokens != null) {
                deviceTokens.forEach(this::deviceToken);
            }
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder platform(PushPlatform platform) {
            this.platform = platform;
            return this;
        }

        public Builder ios() {
            this.platform = PushPlatform.IOS;
            return this;
        }

        public Builder android() {
            this.platform = PushPlatform.ANDROID;
            return this;
        }

        public Builder allPlatforms() {
            this.platform = PushPlatform.ALL;
            return this;
        }

        /**
         * 构建 PushMessage 实例
         *
         * @throws IllegalArgumentException 如果必需字段缺失
         */
        public PushMessage build() {
            // 验证必需字段
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("推送内容不能为空");
            }
            if (deviceTokens.isEmpty()) {
                throw new IllegalArgumentException("设备token列表不能为空");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("推送标题不能为空");
            }
            if (platform == null) {
                throw new IllegalArgumentException("推送平台不能为空");
            }

            return new PushMessage(this);
        }

        /**
         * 快速构建方法（跳过验证，用于某些特殊场景）
         */
        public PushMessage buildUnsafe() {
            return new PushMessage(this);
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
    public static PushMessage of(String content, List<String> deviceTokens, String title) {
        return builder()
                .content(content)
                .deviceTokens(deviceTokens)
                .title(title)
                .build();
    }

    /**
     * 快速创建方法（指定平台）
     */
    public static PushMessage of(String content, List<String> deviceTokens, String title, PushPlatform platform) {
        return builder()
                .content(content)
                .deviceTokens(deviceTokens)
                .title(title)
                .platform(platform)
                .build();
    }

    /**
     * 单设备推送快速创建
     */
    public static PushMessage toSingleDevice(String content, String deviceToken, String title) {
        return builder()
                .content(content)
                .deviceToken(deviceToken)
                .title(title)
                .build();
    }

    /**
     * iOS推送快速创建
     */
    public static PushMessage toIos(String content, List<String> deviceTokens, String title) {
        return builder()
                .content(content)
                .deviceTokens(deviceTokens)
                .title(title)
                .ios()
                .build();
    }

    /**
     * Android推送快速创建
     */
    public static PushMessage toAndroid(String content, List<String> deviceTokens, String title) {
        return builder()
                .content(content)
                .deviceTokens(deviceTokens)
                .title(title)
                .android()
                .build();
    }

    /**
     * 验证推送消息基本信息是否完整
     */
    public boolean isValid() {
        return getContent() != null && !getContent().trim().isEmpty() &&
                !deviceTokens.isEmpty() &&
                title != null && !title.trim().isEmpty() &&
                platform != null;
    }

    /**
     * 获取设备token数量
     */
    public int getDeviceCount() {
        return deviceTokens.size();
    }

    /**
     * 是否单设备推送
     */
    public boolean isSingleDevice() {
        return deviceTokens.size() == 1;
    }

    /**
     * 是否多设备推送
     */
    public boolean isMultiDevice() {
        return deviceTokens.size() > 1;
    }

    /**
     * 是否iOS推送
     */
    public boolean isIos() {
        return platform == PushPlatform.IOS;
    }

    /**
     * 是否Android推送
     */
    public boolean isAndroid() {
        return platform == PushPlatform.ANDROID;
    }

    /**
     * 是否全平台推送
     */
    public boolean isAllPlatforms() {
        return platform == PushPlatform.ALL;
    }
}
