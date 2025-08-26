package com.aicyiframework.core.message;

/**
 * @Description: 业务描述
 * @Author: Mr.Min
 * @Date: 2025/8/23
 **/
public class WechatMpConfig {
    private String appId;
    private String appSecret;
    private String token;
    private String aesKey;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
}
