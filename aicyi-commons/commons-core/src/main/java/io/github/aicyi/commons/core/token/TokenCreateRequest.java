package io.github.aicyi.commons.core.token;

import io.github.aicyi.commons.core.DtoBean;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description
 * @date 2026/5/11
 **/
public class TokenCreateRequest<P> implements DtoBean {

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
    private long ttl;

    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

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

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return "TokenCreateRequest{" +
                "principal=" + principal +
                ", attributes=" + attributes +
                ", ttl=" + ttl +
                ", timeUnit=" + timeUnit +
                '}';
    }
}