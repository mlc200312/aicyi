package com.aicyiframework.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 短信消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmsMessage extends Message {
    private List<String> phoneNumbers; // 手机号列表
    private String templateCode; // 短信模板ID
    private Map<String, String> templateParams; // 模板参数
    private String signName; // 短信签名
}
