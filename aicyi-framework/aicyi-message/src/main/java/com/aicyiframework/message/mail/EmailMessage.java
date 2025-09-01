package com.aicyiframework.message.mail;

import com.aichuangyi.commons.core.message.AbstractMessage;
import com.aichuangyi.commons.core.message.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Min
 * @description 邮件消息
 * @date 2025/8/25
 **/
public class EmailMessage extends AbstractMessage<String> {
    private final String subject;
    private final List<String> toList;
    private final List<String> ccList;
    private final List<Attachment> attachments;
    private final boolean html;

    // 私有构造函数，只能通过Builder创建
    private EmailMessage(Builder builder) {
        super(builder.content, MessageType.EMAIL);
        this.subject = builder.subject;
        this.toList = builder.toList;
        this.ccList = builder.ccList;
        this.attachments = builder.attachments;
        this.html = builder.html;
    }

    // Getter 方法
    public String getSubject() {
        return subject;
    }

    public List<String> getToList() {
        return toList;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public boolean isHtml() {
        return html;
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private String content;
        private String subject;
        private List<String> toList = new ArrayList<>();
        private List<String> ccList = new ArrayList<>();
        private List<Attachment> attachments = new ArrayList<>();
        private boolean html = false;

        public Builder() {
            // 空构造
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
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

        public Builder attachment(Attachment attachment) {
            if (attachment != null) {
                this.attachments.add(attachment);
            }
            return this;
        }

        public Builder attachments(List<Attachment> attachments) {
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

        /**
         * 构建 EmailMessage 实例
         *
         * @throws IllegalArgumentException 如果必需字段缺失
         */
        public EmailMessage build() {
            // 验证必需字段
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("邮件内容不能为空");
            }
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalArgumentException("邮件主题不能为空");
            }
            if (toList.isEmpty()) {
                throw new IllegalArgumentException("收件人列表不能为空");
            }

            return new EmailMessage(this);
        }

        /**
         * 快速构建方法（跳过验证，用于某些特殊场景）
         */
        public EmailMessage buildUnsafe() {
            return new EmailMessage(this);
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
    public static EmailMessage of(String content, String subject, String to) {
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
        return subject != null && !subject.trim().isEmpty() &&
                !toList.isEmpty() &&
                getContent() != null && !getContent().trim().isEmpty();
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
    public void addAttachment(Attachment attachment) {
        if (attachment != null) {
            attachments.add(attachment);
        }
    }
}