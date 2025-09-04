package io.github.aicyi.midware.message.mail;

import io.github.aicyi.commons.lang.BaseBean;

import java.io.File;

/**
 * @author Mr.Min
 * @description 邮件附件
 * @date 2025/8/25
 **/
public class Attachment extends BaseBean {
    private String name; // 附件名称
    private File file; // 附件内容
    private String contentType; // 内容类型

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
