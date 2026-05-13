package io.github.aicyi.midware.message.mail.model;

import io.github.aicyi.midware.message.core.model.AbstractMessage;
import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.model.MessageType;
import io.github.aicyi.commons.core.template.TemplateRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 邮件消息
 * @date 2025/8/25
 **/
public class MailMessage extends AbstractMessage<String> implements MessageContent<String>, TemplateRequest {
    private final List<String> toList;
    private final List<String> ccList;
    private final String subject;
    private final List<MailAttachment> attachments;
    private final boolean html;
    private final String templateId; // 邮件模板ID
    private final Map<String, Object> templateParams; // 模板参数

    // 私有构造函数，只能通过Builder创建
    private MailMessage(Builder builder) {
        super(builder.content, MessageType.MAIL);
        this.toList = builder.toList;
        this.ccList = builder.ccList;
        this.subject = builder.subject;
        this.attachments = builder.attachments;
        this.html = builder.html;
        this.templateId = builder.templateId;
        this.templateParams = builder.templateParams;
    }

    // Getter 方法
    public List<String> getToList() {
        return toList;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public String getSubject() {
        return subject;
    }

    public List<MailAttachment> getAttachments() {
        return attachments;
    }

    public boolean isHtml() {
        return html;
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
        private List<String> toList = new ArrayList<>();
        private List<String> ccList = new ArrayList<>();
        private String subject;
        private String content;
        private List<MailAttachment> attachments = new ArrayList<>();
        private boolean html = false;
        private String templateId;
        private Map<String, Object> templateParams = new HashMap<>(); // 模板参数

        public Builder() {
            // 空构造
        }

        public Builder to(String to) {
            if (to != null && !to.trim().isEmpty()) {
                this.toList.add(to.trim());
            }
            return this;
        }

        public Builder to(List<String> toList) {
            if (toList != null) {
                toList.forEach(this::to);
            }
            return this;
        }

        public Builder cc(String cc) {
            if (cc != null && !cc.trim().isEmpty()) {
                this.ccList.add(cc.trim());
            }
            return this;
        }

        public Builder cc(List<String> ccList) {
            if (ccList != null) {
                ccList.forEach(this::cc);
            }
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder attachment(MailAttachment attachment) {
            if (attachment != null) {
                this.attachments.add(attachment);
            }
            return this;
        }

        public Builder attachments(List<MailAttachment> attachments) {
            if (attachments != null) {
                this.attachments.addAll(attachments);
            }
            return this;
        }

        public Builder html(boolean html) {
            this.html = html;
            return this;
        }

        public Builder html() {
            this.html = true;
            return this;
        }

        public Builder text() {
            this.html = false;
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
         * 构建 EmailMessage 实例
         *
         * @throws IllegalArgumentException 如果必需字段缺失
         */
        public MailMessage build() {
            // 验证必需字段
            if (toList.isEmpty()) {
                throw new IllegalArgumentException("收件人列表不能为空");
            }

            boolean isTemplateMessage = templateId != null && !templateId.trim().isEmpty();

            boolean hasContent = content != null && !content.trim().isEmpty();

            if (!isTemplateMessage && !hasContent) {
                throw new IllegalArgumentException("邮件内容或者邮件模板ID不能为空");
            }

            boolean hasSubject = subject != null && !subject.trim().isEmpty();

            if (!isTemplateMessage && !hasSubject) {
                throw new IllegalArgumentException("邮件主题不能为空");
            }

            return new MailMessage(this);
        }

        /**
         * 快速构建方法（跳过验证，用于某些特殊场景）
         */
        public MailMessage buildUnsafe() {
            return new MailMessage(this);
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
    public static MailMessage of(String to, String templateId, Map<String, Object> templateParams) {
        return builder()
                .to(to)
                .templateId(templateId)
                .templateParams(templateParams)
                .build();
    }

    /**
     * 快速创建方法（使用模板参数）
     */
    public static MailMessage of(List<String> toList, String templateId, Map<String, Object> templateParams) {
        return builder()
                .to(toList)
                .templateId(templateId)
                .templateParams(templateParams)
                .build();
    }

    /**
     * 快速创建方法（直接内容，不使用模板）
     */
    public static MailMessage withContent(String content, String subject, String to) {
        return builder()
                .content(content)
                .subject(subject)
                .to(to)
                .build();
    }

    /**
     * 验证邮件基本信息是否完整
     */
    public boolean isValid() {

        boolean hasSubject = subject != null && !subject.trim().isEmpty();

        return !toList.isEmpty() && ((hasSubject && isContentMessage()) || isTemplateMessage());
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
     * 添加收件人（构建后操作）
     */
    public void addTo(String recipient) {
        if (recipient != null && !recipient.trim().isEmpty()) {
            toList.add(recipient.trim());
        }
    }

    /**
     * 添加抄送人（构建后操作）
     */
    public void addCc(String recipient) {
        if (recipient != null && !recipient.trim().isEmpty()) {
            ccList.add(recipient.trim());
        }
    }

    /**
     * 添加附件（构建后操作）
     */
    public void addAttachment(MailAttachment attachment) {
        if (attachment != null) {
            attachments.add(attachment);
        }
    }
}