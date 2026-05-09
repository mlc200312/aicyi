package io.github.aicyi.midware.message.core.model;

import io.github.aicyi.commons.lang.BaseEntity;
import io.github.aicyi.commons.lang.type.BooleanType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息模版表:message_template
 */
public class MessageTemplate extends BaseEntity implements Serializable {
    /**
     * 主键:id
     */
    private Long id;

    /**
     * 模版编码:template_code
     */
    private String templateCode;

    /**
     * 模版名称:template_name
     */
    private String templateName;

    /**
     * 消息类型:message_type
     */
    private String messageType;

    /**
     * 模版格式:format
     */
    private String format;

    /**
     * 模版引擎类型:engine_type
     */
    private String engineType;

    /**
     * 模版主题:subject
     */
    private String subject;

    /**
     * 短信签名:signature
     */
    private String signature;

    /**
     * 模版参数:variables
     */
    private String variables;

    /**
     * 是否启用，0:未启用，1:已启用；:enabled
     */
    private BooleanType enabled;

    /**
     * 备注:remark
     */
    private String remark;

    /**
     * 删除标记，0：未删除，1：已删除:deleted
     */
    private BooleanType deleted;

    /**
     * 版本:version
     */
    private Integer version;

    /**
     * 创建时间:create_time
     */
    private LocalDateTime createTime;

    /**
     * 更新时间:update_time
     */
    private LocalDateTime updateTime;

    /**
     * 模版内容:content
     */
    private String content;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public BooleanType getEnabled() {
        return enabled;
    }

    public void setEnabled(BooleanType enabled) {
        this.enabled = enabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BooleanType getDeleted() {
        return deleted;
    }

    public void setDeleted(BooleanType deleted) {
        this.deleted = deleted;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}