package com.aicyiframework.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * APP推送消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PushMessage extends Message {
    private List<String> deviceTokens; // 设备token列表
    private String title; // 推送标题
    private String content; // 推送内容
    private PushPlatform platform; // 推送平台(ALL/IOS/ANDROID)
    private Map<String, String> extras; // 额外数据
}
