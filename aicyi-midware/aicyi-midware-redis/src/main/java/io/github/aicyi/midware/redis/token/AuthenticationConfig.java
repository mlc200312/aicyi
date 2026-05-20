package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.lang.BaseBean;

import java.util.concurrent.TimeUnit;

/**
 * 认证配置
 *
 * @author Mr.Min
 * @date 2023/9/5 16:05
 */
public class AuthenticationConfig extends BaseBean {

    /**
     * JWT密钥
     */
    private final String secretKey;

    /**
     * JWT签发者
     */
    private final String issuer;

    /**
     * JWT主题
     */
    private final String subject;

    /**
     * RefreshToken有效期
     */
    private final long refreshTokenTtl;

    /**
     * RefreshToken时间单位
     */
    private final TimeUnit refreshTokenTimeUnit;

    /**
     * AccessToken有效期
     */
    private final long accessTokenTtl;

    /**
     * AccessToken时间单位
     */
    private final TimeUnit accessTokenTimeUnit;

    /**
     * 是否允许多Token
     */
    private final boolean multiTokenAllowed;

    /**
     * 多Token最大数量
     */
    private final int multiTokenCount;


    private AuthenticationConfig(Builder builder) {
        this.secretKey = builder.secretKey;
        this.issuer = builder.issuer;
        this.subject = builder.subject;
        this.refreshTokenTtl = builder.refreshTokenTtl;
        this.refreshTokenTimeUnit = builder.refreshTokenTimeUnit;
        this.accessTokenTtl = builder.accessTokenTtl;
        this.accessTokenTimeUnit = builder.accessTokenTimeUnit;
        this.multiTokenAllowed = builder.multiTokenAllowed;
        this.multiTokenCount = builder.multiTokenCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String secretKey;
        private String issuer;
        private String subject;

        private long refreshTokenTtl;
        private TimeUnit refreshTokenTimeUnit = TimeUnit.DAYS;

        private long accessTokenTtl;
        private TimeUnit accessTokenTimeUnit = TimeUnit.MINUTES;

        private boolean multiTokenAllowed = false;
        private int multiTokenCount = 1;


        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder refreshTokenTtl(long refreshTokenTtl) {
            this.refreshTokenTtl = refreshTokenTtl;
            return this;
        }

        public Builder refreshTokenTimeUnit(TimeUnit refreshTokenTimeUnit) {
            this.refreshTokenTimeUnit = refreshTokenTimeUnit;
            return this;
        }

        public Builder accessTokenTtl(long accessTokenTtl) {
            this.accessTokenTtl = accessTokenTtl;
            return this;
        }

        public Builder accessTokenTimeUnit(TimeUnit accessTokenTimeUnit) {
            this.accessTokenTimeUnit = accessTokenTimeUnit;
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

        public AuthenticationConfig build() {
            validate();
            return new AuthenticationConfig(this);
        }

        private void validate() {
            if (secretKey == null || secretKey.isEmpty()) {
                throw new IllegalArgumentException("secretKey cannot be null or blank");
            }

            if (refreshTokenTtl <= 0) {
                throw new IllegalArgumentException("refreshTokenTtl must be > 0");
            }

            if (accessTokenTtl <= 0) {
                throw new IllegalArgumentException("accessTokenTtl must be > 0");
            }

            if (multiTokenAllowed && multiTokenCount <= 0) {
                throw new IllegalArgumentException("multiTokenCount must be > 0");
            }
        }
    }

    // Getter
    public String getSecretKey() {
        return secretKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getSubject() {
        return subject;
    }

    public long getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public TimeUnit getRefreshTokenTimeUnit() {
        return refreshTokenTimeUnit;
    }

    public long getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public TimeUnit getAccessTokenTimeUnit() {
        return accessTokenTimeUnit;
    }

    public boolean isMultiTokenAllowed() {
        return multiTokenAllowed;
    }

    public int getMultiTokenCount() {
        return multiTokenCount;
    }
}