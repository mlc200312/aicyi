package com.aicyiframework.message.wechat;

/**
 * @author Mr.Min
 * @description 微信模板数据类
 * @date 2025/8/25
 **/
public class WechatTemplateData {
    private String value;
    private String color;

    public WechatTemplateData(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
