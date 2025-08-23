package com.aicyiframework.core.message;

import lombok.Data;

/**
 * 邮件附件
 */
@Data
public class Attachment {
    private String name; // 附件名称
    private byte[] content; // 附件内容
    private String contentType; // 内容类型
}
