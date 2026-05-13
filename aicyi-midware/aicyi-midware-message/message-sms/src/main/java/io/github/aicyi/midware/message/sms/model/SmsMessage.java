package io.github.aicyi.midware.message.sms.model;

import io.github.aicyi.midware.message.core.model.AbstractMessage;
import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.model.MessageType;
import io.github.aicyi.commons.core.template.TemplateRequest;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Mr.Min
 * @description 短信消息
 * @date 2025/8/25
 **/
public class SmsMessage extends AbstractMessage<String> implements MessageContent<String>, TemplateRequest {
    private final List<String> phoneNumbers; // 手机号列表
    private final String sign; // 短信签名
    private final String templateId; // 短信模板ID
    private final Map<String, Object> templateParams; // 模板参数

    // 私有构造函数，只能通过Builder创建
    private SmsMessage(Builder builder) {
        super(builder.content, MessageType.SMS);
        this.phoneNumbers = builder.phoneNumbers;
        this.sign = builder.sign;
        this.templateId = builder.templateId;
        this.templateParams = builder.templateParams;
    }

    // Getter 方法
    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public String getTemplateId() {
        return templateId;
    }

    @Override
    public Map<String, Object> getTemplateParams() {
        return templateParams;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private String content;
        private List<String> phoneNumbers = new ArrayList<>();
        private String sign;
        private String templateId;
        private Map<String, Object> templateParams = new HashMap<>();

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

        public Builder sign(String sign) {
            this.sign = sign;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder templateParam(String key, Object value) {
            if (key != null && value != null) {
                this.templateParams.put(key.trim(), value);
            }
            return this;
        }

        public Builder templateParams(Map<String, Object> templateParams) {
            if (templateParams != null) {
                templateParams.forEach(this::templateParam);
            }
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

            boolean isTemplateMessage = templateId != null && !templateId.trim().isEmpty();

            boolean hasContent = content != null && !content.trim().isEmpty();

            if (!isTemplateMessage && !hasContent) {
                throw new IllegalArgumentException("短信内容或者短信模板ID不能为空");
            }

            boolean hasSign = sign != null && !sign.trim().isEmpty();

            if (!isTemplateMessage && !hasSign) {
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
    public static SmsMessage of(List<String> phoneNumbers, String templateId, Map<String, Object> templateParams) {
        return builder()
                .phoneNumbers(phoneNumbers)
                .templateId(templateId)
                .templateParams(templateParams)
                .build();
    }

    /**
     * 快速创建方法（单手机号）
     */
    public static SmsMessage of(String phoneNumber, String templateId, Map<String, Object> templateParams) {
        return builder()
                .phoneNumber(phoneNumber)
                .templateId(templateId)
                .templateParams(templateParams)
                .build();
    }

    /**
     * 快速创建方法（直接内容，不使用模板）
     */
    public static SmsMessage withContent(String content, List<String> phoneNumbers, String sign) {
        return builder()
                .content(content)
                .phoneNumbers(phoneNumbers)
                .sign(sign)
                .build();
    }

    /**
     * 快速创建方法（直接内容，不使用模板）
     */
    public static SmsMessage withContent(String content, String phoneNumber, String sign) {
        return builder()
                .content(content)
                .phoneNumber(phoneNumber)
                .sign(sign)
                .build();
    }

    /**
     * 验证短信消息基本信息是否完整
     */
    public boolean isValid() {

        boolean hasSign = sign != null && !sign.trim().isEmpty();

        return !phoneNumbers.isEmpty() && (isTemplateMessage() || (isContentMessage() && hasSign));
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
