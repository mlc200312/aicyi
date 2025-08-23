package com.aicyiframework.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 邮件消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailMessage extends Message {
    private List<String> to; // 收件人列表
    private String subject; // 邮件主题
    private String content; // 邮件内容
    private boolean html = false; // 是否HTML格式
    private List<Attachment> attachments; // 附件列表
}
