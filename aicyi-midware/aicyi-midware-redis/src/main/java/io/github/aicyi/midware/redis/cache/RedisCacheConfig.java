package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.PrincipalSerializer;
import io.github.aicyi.commons.core.cache.CacheConfig;
import io.github.aicyi.commons.lang.model.CacheWrapper;
import io.github.aicyi.commons.util.Assert;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 缓存配置类
 * @date 2025/8/12
 **/
public final class RedisCacheConfig implements CacheConfig<CacheWrapper<Object>> {

    /**
     * 全局缓存前缀
     */
    private final String globalPrefix;

    /**
     * 缓存名称
     */
    private final String cacheName;

    /**
     * TTL（建议必填）
     */
    private final Duration ttl;

    /**
     * 是否缓存空值
     */
    private final boolean cacheNull;

    /**
     * 是否启用 TTL 抖动
     */
    private final boolean ttlJitter;

    /**
     * 10 = ±10%
     */
    private final int jitterPercent;

    /**
     * 防击穿锁 TTL
     */
    private final Duration lockTtl;

    /**
     * 等待缓存回填超时时间
     */
    private final Duration waitTimeout;

    /**
     * 缓存值序列化器
     */
    private PrincipalSerializer<CacheWrapper<Object>> serializer;

    private RedisCacheConfig(Builder builder) {
        this.globalPrefix = builder.globalPrefix;
        this.cacheName = builder.cacheName;
        this.ttl = builder.ttl;
        this.cacheNull = builder.cacheNull;
        this.ttlJitter = builder.ttlJitter;
        this.jitterPercent = builder.jitterPercent;
        this.lockTtl = builder.lockTtl;
        this.waitTimeout = builder.waitTimeout;
        this.serializer = builder.serializer;

        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validate() {
        Assert.notNull(globalPrefix, "globalPrefix");
        Assert.notNull(cacheName, "cacheName");
        Assert.notNull(ttl, "ttl");
        Assert.notNull(lockTtl, "lockTtl");
        Assert.notNull(waitTimeout, "waitTimeout");
        Assert.notNegative(ttl.toMillis(), "ttl");
        Assert.check(jitterPercent >= 0 && jitterPercent <= 50, "jitterPercent must be between 0 and 50");
        Assert.notNull(serializer, "serializer");
    }

    public String getGlobalPrefix() {
        return globalPrefix;
    }

    public String getCacheName() {
        return cacheName;
    }

    public Duration getTtl() {
        return ttl;
    }

    public boolean isCacheNull() {
        return cacheNull;
    }

    public boolean isTtlJitter() {
        return ttlJitter;
    }

    public int getJitterPercent() {
        return jitterPercent;
    }

    public Duration getLockTtl() {
        return lockTtl;
    }

    public Duration getWaitTimeout() {
        return waitTimeout;
    }

    public PrincipalSerializer<CacheWrapper<Object>> getSerializer() {
        return serializer;
    }

    public static final class Builder {

        private String globalPrefix;

        private String cacheName;

        private Duration ttl;

        private boolean cacheNull = false;

        private boolean ttlJitter = true;

        /**
         * 10 = ±10%
         */
        private int jitterPercent = 10;

        /**
         * 防击穿锁
         */
        private Duration lockTtl = Duration.ofSeconds(10);

        /**
         * 等待缓存回填
         */
        private Duration waitTimeout = Duration.ofSeconds(3);

        /**
         * 缓存值序列化器
         */
        private PrincipalSerializer<CacheWrapper<Object>> serializer;

        private Builder() {
        }

        public Builder globalPrefix(String globalPrefix) {
            this.globalPrefix = globalPrefix;
            return this;
        }

        public Builder cacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public Builder ttl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder cacheNull(boolean cacheNull) {
            this.cacheNull = cacheNull;
            return this;
        }

        public Builder ttlJitter(boolean ttlJitter) {
            this.ttlJitter = ttlJitter;
            return this;
        }

        public Builder jitterPercent(int jitterPercent) {
            this.jitterPercent = jitterPercent;
            return this;
        }

        public Builder lockTtl(Duration lockTtl) {
            this.lockTtl = lockTtl;
            return this;
        }

        public Builder waitTimeout(Duration waitTimeout) {
            this.waitTimeout = waitTimeout;
            return this;
        }

        public Builder serializer(PrincipalSerializer<CacheWrapper<Object>> serializer) {
            this.serializer = serializer;
            return this;
        }

        public RedisCacheConfig build() {
            return new RedisCacheConfig(this);
        }
    }
}