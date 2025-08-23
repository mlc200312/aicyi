package com.aicyiframework.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class WechatMpMessage extends Message {
    private List<String> openIds;
    private String templateId;
    private Map<String, WechatTemplateData> data;
    private String page;
    private String miniprogramState;
}

