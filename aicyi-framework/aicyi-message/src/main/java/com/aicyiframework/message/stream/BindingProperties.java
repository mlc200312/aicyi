package com.aicyiframework.message.stream;

import com.aichuangyi.commons.lang.BaseBean;

/**
 * @author Mr.Min
 * @description MQ Binding 属性
 * @date 2025/8/28
 **/
public class BindingProperties extends BaseBean {
    private String destination;
    private String group;
    private String contentType;

    // getters and setters
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}