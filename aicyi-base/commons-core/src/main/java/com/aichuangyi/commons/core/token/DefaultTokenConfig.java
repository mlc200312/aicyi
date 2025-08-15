package com.aichuangyi.commons.core.token;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Token配置默认实现
 * @date 2025/8/13
 **/
public class DefaultTokenConfig implements TokenConfig {

    // 默认最大Token数量
    private static final int DEFAULT_MULTI_TOKEN_COUNT = 3;

    // 默认过期时间(1小时)
    private static final long DEFAULT_EXPIRE_TIME = 1;
    private static final TimeUnit DEFAULT_EXPIRE_UNIT = TimeUnit.HOURS;

    // 默认刷新窗口(30分钟)
    private static final long DEFAULT_REFRESH_WINDOW = 30;
    private static final TimeUnit DEFAULT_REFRESH_UNIT = TimeUnit.MINUTES;

    private final String issuer;
    private final String signingKey;
    private final boolean multiTokenAllowed;
    private final int multiTokenCount;
    private final long defaultExpire;
    private final TimeUnit defaultExpireUnit;
    private final long refreshWindow;
    private final TimeUnit refreshWindowUnit;

    /**
     * 使用默认配置的构造器
     *
     * @param issuer     签发者
     * @param signingKey 签名密钥
     */
    public DefaultTokenConfig(String issuer, String signingKey) {
        this(issuer, signingKey, true);
    }

    /**
     * 完整参数构造器
     *
     * @param issuer            签发者
     * @param signingKey        签名密钥
     * @param multiTokenAllowed 是否允许多Token
     */
    public DefaultTokenConfig(String issuer, String signingKey, boolean multiTokenAllowed) {
        this(issuer, signingKey, multiTokenAllowed, DEFAULT_MULTI_TOKEN_COUNT,
                DEFAULT_EXPIRE_TIME, DEFAULT_EXPIRE_UNIT,
                DEFAULT_REFRESH_WINDOW, DEFAULT_REFRESH_UNIT);
    }

    /**
     * 全参数构造器
     */
    public DefaultTokenConfig(String issuer, String signingKey, boolean multiTokenAllowed, int multiTokenCount,
                              long defaultExpire, TimeUnit defaultExpireUnit, long refreshWindow, TimeUnit refreshWindowUnit) {
        this.issuer = issuer;
        this.signingKey = signingKey;
        this.multiTokenAllowed = multiTokenAllowed;
        this.multiTokenCount = multiTokenCount;
        this.defaultExpire = defaultExpire;
        this.defaultExpireUnit = defaultExpireUnit;
        this.refreshWindow = refreshWindow;
        this.refreshWindowUnit = refreshWindowUnit;
    }

    @Override
    public long getDefaultExpire(TimeUnit unit) {
        return unit.convert(defaultExpire, defaultExpireUnit);
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public String getSigningKey() {
        return signingKey;
    }

    @Override
    public long getRefreshWindow(TimeUnit unit) {
        return unit.convert(refreshWindow, refreshWindowUnit);
    }

    @Override
    public boolean isMultiTokenAllowed() {
        return multiTokenAllowed;
    }

    @Override
    public int getMultiTokenCount() {
        return multiTokenCount;
    }

    // Builder模式
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String issuer = "default-issuer";
        private String signingKey;
        private boolean multiTokenAllowed = true;
        private int multiTokenCount = DEFAULT_MULTI_TOKEN_COUNT;
        private long defaultExpire = DEFAULT_EXPIRE_TIME;
        private TimeUnit defaultExpireUnit = DEFAULT_EXPIRE_UNIT;
        private long refreshWindow = DEFAULT_REFRESH_WINDOW;
        private TimeUnit refreshWindowUnit = DEFAULT_REFRESH_UNIT;

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder signingKey(String signingKey) {
            this.signingKey = signingKey;
            return this;
        }

        public Builder multiTokenAllowed(boolean multiTokenAllowed) {
            this.multiTokenAllowed = multiTokenAllowed;
            return this;
        }

        public Builder multiTokenCount(int multiTokenCount) {
            this.multiTokenCount = multiTokenCount;
            return this;
        }

        public Builder defaultExpire(long defaultExpire, TimeUnit unit) {
            this.defaultExpire = defaultExpire;
            this.defaultExpireUnit = unit;
            return this;
        }

        public Builder refreshWindow(long refreshWindow, TimeUnit unit) {
            this.refreshWindow = refreshWindow;
            this.refreshWindowUnit = unit;
            return this;
        }

        public DefaultTokenConfig build() {
            return new DefaultTokenConfig(
                    issuer, signingKey,
                    multiTokenAllowed, multiTokenCount,
                    defaultExpire, defaultExpireUnit,
                    refreshWindow, refreshWindowUnit
            );
        }
    }
}
