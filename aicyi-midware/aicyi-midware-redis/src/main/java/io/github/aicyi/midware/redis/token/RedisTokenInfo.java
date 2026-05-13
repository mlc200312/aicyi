package io.github.aicyi.midware.redis.token;

import java.util.Map;

/**
 * Redis Token信息
 *
 * @param <P> principal类型
 */
public class RedisTokenInfo<P> {

    /**
     * Token
     */
    private String token;

    /**
     * 主体信息
     */
    private P principal;

    /**
     * Token属性
     */
    private Map<String, Object> attributes;

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

    public void setAttributes(
            Map<String, Object> attributes) {

        this.attributes = attributes;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}