package com.aicyiframework.core.message;


/**
 * @Description: 微信模版
 * @Author: Mr.Min
 * @Date: 2025/8/23
 **/
public class WechatTemplate {
    private String templateId;
    private String title;
    private String content;
    private String example;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
