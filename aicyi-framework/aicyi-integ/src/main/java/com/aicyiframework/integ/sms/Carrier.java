package com.aicyiframework.integ.sms;

/**
 * @author Mr.Min
 * @description 运营商枚举类
 * @date 2025/8/26
 **/
public enum Carrier {
    CHINA_MOBILE("@139.com"),
    CHINA_TELECOM("@189.cn"),
    CHINA_UNICOM("@wo.com.cn");

    private final String gatewayDomain;

    Carrier(String gatewayDomain) {
        this.gatewayDomain = gatewayDomain;
    }

    public String getGatewayDomain() {
        return gatewayDomain;
    }
}