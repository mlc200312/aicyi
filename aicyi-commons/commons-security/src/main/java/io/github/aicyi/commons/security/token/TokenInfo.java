package io.github.aicyi.commons.security.token;

import java.util.Map;

/**
 * @author Mr.Min
 * @description Token会话
 * @date 16:44
 **/
public class TokenInfo<P> implements TokenSession<P> {

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
     * 登录时间
     */
    private long issuedAt;

    /**
     * 过期时间
     */
    private long expireAt;


    @Override
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public P getPrincipal() {
        return principal;
    }

    public void setPrincipal(P principal) {
        this.principal = principal;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
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
