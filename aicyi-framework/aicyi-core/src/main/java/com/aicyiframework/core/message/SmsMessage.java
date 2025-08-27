package com.aicyiframework.core.message;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Mr.Min
 * @description 短信消息
 * @date 2025/8/25
 **/
public class SmsMessage extends Message<String> {
    private final List<String> phoneNumbers; // 手机号列表
    private final String templateId; // 短信模板ID
    private final Map<String, String> templateParams; // 模板参数
    private final String signName; // 短信签名

    // 私有构造函数，只能通过Builder创建
    private SmsMessage(Builder builder) {
        super(builder.content, MessageType.SMS);
        this.phoneNumbers = builder.phoneNumbers;
        this.templateId = builder.templateId;
        this.templateParams = builder.templateParams;
        this.signName = builder.signName;
    }

    // Getter 方法
    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getTemplateId() {
        return templateId;
    }

    public Map<String, String> getTemplateParams() {
        return templateParams;
    }

    public String getSignName() {
        return signName;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private String content;
        private List<String> phoneNumbers = new ArrayList<>();
        private String templateId;
        private Map<String, String> templateParams = new HashMap<>();
        private String signName;

        public Builder() {
            // 空构造
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                this.phoneNumbers.add(phoneNumber.trim());
            }
            return this;
        }

        public Builder phoneNumbers(List<String> phoneNumbers) {
            if (phoneNumbers != null) {
                phoneNumbers.forEach(this::phoneNumber);
            }
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder templateParam(String key, String value) {
            if (key != null && value != null) {
                this.templateParams.put(key.trim(), value.trim());
            }
            return this;
        }

        public Builder templateParams(Map<String, String> templateParams) {
            if (templateParams != null) {
                templateParams.forEach(this::templateParam);
            }
            return this;
        }

        public Builder signName(String signName) {
            this.signName = signName;
            return this;
        }

        /**
         * 构建 SmsMessage 实例
         *
         * @throws IllegalArgumentException 如果必需字段缺失
         */
        public SmsMessage build() {
            // 验证必需字段
            if (phoneNumbers.isEmpty()) {
                throw new IllegalArgumentException("手机号列表不能为空");
            }
            if (content == null && (templateId == null || templateId.trim().isEmpty())) {
                throw new IllegalArgumentException("短信内容或者短信模板ID不能为空");
            }
            if (signName == null || signName.trim().isEmpty()) {
                throw new IllegalArgumentException("短信签名不能为空");
            }

            return new SmsMessage(this);
        }

        /**
         * 快速构建方法（跳过验证，用于某些特殊场景）
         */
        public SmsMessage buildUnsafe() {
            return new SmsMessage(this);
        }
    }

    /**
     * 静态工厂方法，创建Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 快速创建方法（使用模板参数）
     */
    public static SmsMessage of(String templateId, List<String> phoneNumbers,
                                Map<String, String> templateParams, String signName) {
        return builder()
                .templateId(templateId)
                .phoneNumbers(phoneNumbers)
                .templateParams(templateParams)
                .signName(signName)
                .build();
    }

    /**
     * 快速创建方法（单手机号）
     */
    public static SmsMessage of(String templateId, String phoneNumber,
                                Map<String, String> templateParams, String signName) {
        return builder()
                .templateId(templateId)
                .phoneNumber(phoneNumber)
                .templateParams(templateParams)
                .signName(signName)
                .build();
    }

    /**
     * 快速创建方法（直接内容，不使用模板）
     */
    public static SmsMessage withContent(String content, List<String> phoneNumbers, String signName) {
        return builder()
                .content(content)
                .phoneNumbers(phoneNumbers)
                .signName(signName)
                .build();
    }

    /**
     * 快速创建方法（直接内容，不使用模板）
     */
    public static SmsMessage withContent(String content, String phoneNumber, String signName) {
        return builder()
                .content(content)
                .phoneNumber(phoneNumber)
                .signName(signName)
                .build();
    }

    /**
     * 验证短信消息基本信息是否完整
     */
    public boolean isValid() {
        return !phoneNumbers.isEmpty() &&
                (isTemplateMessage() || isContentMessage()) &&
                signName != null && !signName.trim().isEmpty();
    }

    /**
     * 是否使用模板发送
     */
    public boolean isTemplateMessage() {
        return templateId != null && !templateId.trim().isEmpty();
    }

    /**
     * 是否直接内容发送
     */
    public boolean isContentMessage() {
        return getContent() != null && !getContent().trim().isEmpty();
    }

    /**
     * 是否包含模板参数
     */
    public boolean hasTemplateParams() {
        return !templateParams.isEmpty();
    }

    /**
     * 获取手机号数量
     */
    public int getPhoneNumberCount() {
        return phoneNumbers.size();
    }

    /**
     * 是否单手机号发送
     */
    public boolean isSinglePhone() {
        return phoneNumbers.size() == 1;
    }

    /**
     * 是否多手机号发送
     */
    public boolean isMultiPhone() {
        return phoneNumbers.size() > 1;
    }

    /**
     * 获取第一个手机号（单发时使用）
     */
    public String getFirstPhoneNumber() {
        return phoneNumbers.isEmpty() ? null : phoneNumbers.get(0);
    }
}
