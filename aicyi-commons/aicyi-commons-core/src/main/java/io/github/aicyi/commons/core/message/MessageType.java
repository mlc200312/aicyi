package io.github.aicyi.commons.core.message;

import io.github.aicyi.commons.lang.StringEnumType;

/**
 * @author Mr.Min
 * @description 消息类型
 * @date 2025/8/25
 **/
public enum MessageType implements StringEnumType {
    MAIL("mail", "邮件"),
    SMS("sms", "短信"),
    PUSH("push", "推送"),
    MQ("mq", "消息队列"),
    WECHAT_MP("wechat_mp", "微信公众号")
    ;

    private final String code;
    private final String description;

    MessageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}