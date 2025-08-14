package com.aichuangyi.core.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 缓存配置类
 * @date 2025/8/12
 **/
public class CacheConfig {

    private long defaultExpireTime = 3600; // 默认过期时间(秒)
    private TimeUnit defaultTimeUnit = TimeUnit.SECONDS; // 默认时间单位
    private int maxSize = 10000; // 最大缓存数量
    private boolean recordStats = false; // 是否记录统计信息

    public long getDefaultExpireTime() {
        return defaultExpireTime;
    }

    public void setDefaultExpireTime(long defaultExpireTime) {
        this.defaultExpireTime = defaultExpireTime;
    }

    public TimeUnit getDefaultTimeUnit() {
        return defaultTimeUnit;
    }

    public void setDefaultTimeUnit(TimeUnit defaultTimeUnit) {
        this.defaultTimeUnit = defaultTimeUnit;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isRecordStats() {
        return recordStats;
    }

    public void setRecordStats(boolean recordStats) {
        this.recordStats = recordStats;
    }

    public static CacheConfig defaultConfig() {
        return new CacheConfig();
    }

    public static CacheConfig custom() {
        return new CacheConfig();
    }

    public CacheConfig expireAfterWrite(long duration, TimeUnit unit) {
        this.defaultExpireTime = duration;
        this.defaultTimeUnit = unit;
        return this;
    }

    public CacheConfig maximumSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public CacheConfig recordStats(boolean recordStats) {
        this.recordStats = recordStats;
        return this;
    }
}