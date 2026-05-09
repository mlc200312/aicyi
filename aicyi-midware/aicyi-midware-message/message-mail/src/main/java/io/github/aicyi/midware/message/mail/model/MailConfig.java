package io.github.aicyi.midware.message.mail.model;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.util.Assert;

/**
 * @author Mr.Min
 * @description 邮件配置类
 * @date 2025/8/25
 */
public class MailConfig extends BaseBean {

    /**
     * SMTP Host
     */
    private String host;

    /**
     * SMTP Port
     */
    private int port = 25;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 发件邮箱
     */
    private String fromAddress;

    /**
     * 发件人名称
     */
    private String fromName;

    /**
     * 是否启用 SSL
     */
    private boolean sslEnabled = false;

    /**
     * 是否启用 TLS
     */
    private boolean tlsEnabled = false;

    /**
     * 连接超时时间（毫秒）
     */
    private int connectionTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int timeout = 10000;

    /**
     * 编码
     */
    private String charset = "UTF-8";

    private MailConfig() {
    }

    private MailConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.username = builder.username;
        this.password = builder.password;
        this.fromAddress = builder.fromAddress;
        this.fromName = builder.fromName;
        this.sslEnabled = builder.sslEnabled;
        this.tlsEnabled = builder.tlsEnabled;
        this.connectionTimeout = builder.connectionTimeout;
        this.timeout = builder.timeout;
        this.charset = builder.charset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String host;

        private int port = 25;

        private String username;

        private String password;

        private String fromAddress;

        private String fromName;

        private boolean sslEnabled = false;

        private boolean tlsEnabled = false;

        private int connectionTimeout = 5000;

        private int timeout = 10000;

        private String charset = "UTF-8";

        private Builder() {
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder fromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public Builder fromName(String fromName) {
            this.fromName = fromName;
            return this;
        }

        public Builder sslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
            return this;
        }

        public Builder tlsEnabled(boolean tlsEnabled) {
            this.tlsEnabled = tlsEnabled;
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder charset(String charset) {
            this.charset = charset;
            return this;
        }

        public MailConfig build() {

            validate();

            return new MailConfig(this);
        }

        /**
         * 校验必填参数
         */
        private void validate() {

            Assert.notBlank(host, "mail host");

            Assert.notBlank(username, "mail username");

            Assert.notBlank(password, "mail password");

            Assert.notBlank(fromAddress, "mail fromAddress");

            Assert.notNegative(connectionTimeout, "mail connectionTimeout");

            Assert.notNegative(timeout, "mail timeout");
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getCharset() {
        return charset;
    }
}