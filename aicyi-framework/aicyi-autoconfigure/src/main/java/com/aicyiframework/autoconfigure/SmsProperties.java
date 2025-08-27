package com.aicyiframework.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:35
 **/
@ConfigurationProperties(
        prefix = "aicyi.sms"
)
public class SmsProperties {
    private boolean enabled;
    private String username;
    private String password;
    private String from;
    private Map<String, String> properties;

    public SmsProperties() {
        this.properties = new HashMap<>();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
