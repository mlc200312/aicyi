package io.github.aicyi.commons.security.token;

import io.github.aicyi.commons.core.DtoBean;

import java.util.Map;

/**
 * @param <P> Principal类型
 * @author Mr.Min
 * @description RefreshToken会话
 * @date 2025/8/12
 */
public class TokenInfo<P> implements DtoBean {

    /**
     * RefreshToken
     */
    private String token;

    /**
     * Principal
     */
    private P principal;

    /**
     * 自定义属性
     */
    private Map<String, Object> attributes;

    /**
     * 登录设备ID
     */
    private String deviceId;

    /**
     * 登录时间
     */
    private long issuedAt;

    /**
     * 过期时间
     */
    private long expireAt;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public P getPrincipal() {
        return principal;
    }

    public void setPrincipal(P principal) {
        this.principal = principal;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}