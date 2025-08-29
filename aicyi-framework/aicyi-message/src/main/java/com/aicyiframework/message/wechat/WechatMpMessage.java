package com.aicyiframework.message.wechat;

import com.aicyiframework.core.message.AbstractMessage;
import com.aicyiframework.core.message.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 微信公众号消息
 * @date 2025/8/25
 **/
public class WechatMpMessage extends AbstractMessage<Map<String, WechatTemplateData>> {
    private final List<String> openIds;
    private final String templateId;
    private final String page;
    private final String miniProgramState;

    // 私有构造函数，只能通过Builder创建
    private WechatMpMessage(Builder builder) {
        super(builder.content, MessageType.WECHAT_MP);
        this.openIds = builder.openIds;
        this.templateId = builder.templateId;
        this.page = builder.page;
        this.miniProgramState = builder.miniProgramState;
    }

    // Getter 方法
    public List<String> getOpenIds() {
        return openIds;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getPage() {
        return page;
    }

    public String getMiniProgramState() {
        return miniProgramState;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private Map<String, WechatTemplateData> content = new HashMap<>();
        private List<String> openIds = new ArrayList<>();
        private String templateId;
        private String page;
        private String miniProgramState;

        public Builder() {
            // 空构造
        }

        public Builder content(Map<String, WechatTemplateData> content) {
            if (content != null) {
                this.content = content;
            }
            return this;
        }

        public Builder templateData(String key, WechatTemplateData data) {
            if (key != null && data != null) {
                this.content.put(key, data);
            }
            return this;
        }

        public Builder templateData(String key, String value) {
            return templateData(key, value, null);
        }

        public Builder templateData(String key, String value, String color) {
            if (key != null && value != null) {
                this.content.put(key, new WechatTemplateData(value, color));
            }
            return this;
        }

        public Builder openId(String openId) {
            if (openId != null && !openId.trim().isEmpty()) {
                this.openIds.add(openId.trim());
            }
            return this;
        }

        public Builder openIds(List<String> openIds) {
            if (openIds != null) {
                openIds.forEach(this::openId);
            }
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder page(String page) {
            this.page = page;
            return this;
        }

        public Builder miniProgramState(String miniProgramState) {
            this.miniProgramState = miniProgramState;
            return this;
        }

        public Builder formal() {
            this.miniProgramState = "formal";
            return this;
        }

        public Builder trial() {
            this.miniProgramState = "trial";
            return this;
        }

        public Builder developer() {
            this.miniProgramState = "developer";
            return this;
        }

        /**
         * 构建 WechatMpMessage 实例
         *
         * @throws IllegalArgumentException 如果必需字段缺失
         */
        public WechatMpMessage build() {
            // 验证必需字段
            if (openIds.isEmpty()) {
                throw new IllegalArgumentException("OpenID列表不能为空");
            }
            if (templateId == null || templateId.trim().isEmpty()) {
                throw new IllegalArgumentException("模板ID不能为空");
            }
            if (content.isEmpty()) {
                throw new IllegalArgumentException("模板数据内容不能为空");
            }

            return new WechatMpMessage(this);
        }

        /**
         * 快速构建方法（跳过验证，用于某些特殊场景）
         */
        public WechatMpMessage buildUnsafe() {
            return new WechatMpMessage(this);
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
    public static WechatMpMessage of(String templateId, List<String> openIds,
                                     Map<String, WechatTemplateData> content) {
        return builder()
                .templateId(templateId)
                .openIds(openIds)
                .content(content)
                .build();
    }

    /**
     * 快速创建方法（单用户）
     */
    public static WechatMpMessage toSingleUser(String templateId, String openId,
                                               Map<String, WechatTemplateData> content) {
        return builder()
                .templateId(templateId)
                .openId(openId)
                .content(content)
                .build();
    }

    /**
     * 快速创建方法（带跳转页面）
     */
    public static WechatMpMessage withPage(String templateId, List<String> openIds,
                                           Map<String, WechatTemplateData> content, String page) {
        return builder()
                .templateId(templateId)
                .openIds(openIds)
                .content(content)
                .page(page)
                .build();
    }

    /**
     * 验证消息基本信息是否完整
     */
    public boolean isValid() {
        return !openIds.isEmpty() &&
                templateId != null && !templateId.trim().isEmpty() &&
                getContent() != null && !getContent().isEmpty();
    }

    /**
     * 获取用户数量
     */
    public int getUserCount() {
        return openIds.size();
    }

    /**
     * 是否单用户发送
     */
    public boolean isSingleUser() {
        return openIds.size() == 1;
    }

    /**
     * 是否多用户发送
     */
    public boolean isMultiUser() {
        return openIds.size() > 1;
    }

    /**
     * 是否包含跳转页面
     */
    public boolean hasPage() {
        return page != null && !page.trim().isEmpty();
    }

    /**
     * 是否包含小程序状态
     */
    public boolean hasMiniProgramState() {
        return miniProgramState != null && !miniProgramState.trim().isEmpty();
    }

    /**
     * 获取第一个OpenID（单发时使用）
     */
    public String getFirstOpenId() {
        return openIds.isEmpty() ? null : openIds.get(0);
    }

    /**
     * 获取模板数据值
     */
    public String getTemplateDataValue(String key) {
        WechatTemplateData data = getContent().get(key);
        return data != null ? data.getValue() : null;
    }

    /**
     * 获取模板数据颜色
     */
    public String getTemplateDataColor(String key) {
        WechatTemplateData data = getContent().get(key);
        return data != null ? data.getColor() : null;
    }
}

