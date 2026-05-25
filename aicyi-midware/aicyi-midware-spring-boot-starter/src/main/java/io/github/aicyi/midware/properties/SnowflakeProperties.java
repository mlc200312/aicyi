package io.github.aicyi.midware.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 服务名（Redis workerId namespace）
     */
    private String serviceName = "default-service";

    /**
     * workerId bit 数
     * <p>
     * 5 -> max 31
     * 10 -> max 1023
     */
    private int workerIdBits = 5;

    /**
     * datacenterId
     */
    private int datacenterId = 0;

    /**
     * workerId lease TTL
     */
    private long ttlSeconds = 60;

    /**
     * heartbeat 间隔
     * 默认 ttl / 3
     */
    private long heartbeatSeconds = ttlSeconds / 3;

    /**
     * lease 丢失后是否尝试自动恢复
     */
    private boolean autoRecover = false;

    /**
     * 时钟回拨容忍（ms）
     */
    private long clockBackwardToleranceMs = 5;

    /**
     * 自定义 epoch
     */
    private long epoch = 1672531200000L;

    public int getMaxWorkerId() {
        return (1 << workerIdBits) - 1;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getWorkerIdBits() {
        return workerIdBits;
    }

    public void setWorkerIdBits(int workerIdBits) {
        this.workerIdBits = workerIdBits;
    }

    public int getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(int datacenterId) {
        this.datacenterId = datacenterId;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public long getHeartbeatSeconds() {
        return heartbeatSeconds;
    }

    public void setHeartbeatSeconds(long heartbeatSeconds) {
        this.heartbeatSeconds = heartbeatSeconds;
    }

    public boolean isAutoRecover() {
        return autoRecover;
    }

    public void setAutoRecover(boolean autoRecover) {
        this.autoRecover = autoRecover;
    }

    public long getClockBackwardToleranceMs() {
        return clockBackwardToleranceMs;
    }

    public void setClockBackwardToleranceMs(long clockBackwardToleranceMs) {
        this.clockBackwardToleranceMs = clockBackwardToleranceMs;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }
}