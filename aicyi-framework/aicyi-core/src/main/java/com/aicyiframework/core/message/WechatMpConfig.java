package com.aicyiframework.core.message;

import lombok.Data;

/**
 * @Description: 业务描述
 * @Author: Mr.Min
 * @Date: 2025/8/23
 **/
@Data
public class WechatMpConfig {
    private String appId;
    private String appSecret;
    private String token;
    private String aesKey;
}
