package com.aicyiframework.core.message;

// 1. 在MessageType枚举中添加新类型
public enum MessageType {
    EMAIL,
    SMS,
    PUSH,
    MQ,
    WECHAT_MP // 新增微信公众号消息
}