package io.github.aicyi.commons.util.id;

import io.github.aicyi.commons.lang.BaseBean;

/**
 * @author Mr.Min
 * @description Lease 对象
 * @date 2026/5/21
 **/
public class WorkerIdLease extends BaseBean {

    private int workerId;

    /**
     * fencing token
     */
    private String token;

    /**
     * TTL（秒）
     */
    private long ttlSeconds;

    public WorkerIdLease() {
    }

    public WorkerIdLease(int workerId, String token, long ttlSeconds) {
        this.workerId = workerId;
        this.token = token;
        this.ttlSeconds = ttlSeconds;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}