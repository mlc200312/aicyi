package io.github.aicyi.midware.message.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aicyi.message")
public class MessageProperties {

    private SmsProperties sms = new SmsProperties();

    private EmailProperties email = new EmailProperties();

    private MqProperties mq = new MqProperties();

    public SmsProperties getSms() {
        return sms;
    }

    public void setSms(SmsProperties sms) {
        this.sms = sms;
    }

    public EmailProperties getEmail() {
        return email;
    }

    public void setEmail(EmailProperties email) {
        this.email = email;
    }

    public MqProperties getMq() {
        return mq;
    }

    public void setMq(MqProperties mq) {
        this.mq = mq;
    }

    public static class SmsProperties {
        private boolean enabled;
        private String provider = "default";
        private String username;
        private String password;
        private String from;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }
    }

    public static class EmailProperties {
        private boolean enabled;
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String fromName;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFromName() {
            return fromName;
        }

        public void setFromName(String fromName) {
            this.fromName = fromName;
        }
    }

    public static class MqProperties {
        private boolean enabled;
        private String provider;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }
    }
}