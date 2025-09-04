package io.github.aicyi.midware.rabbitmq;

import io.github.aicyi.commons.lang.BaseBean;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Mr.Min
 * @description Stream 配置
 * @date 14:16
 **/
public class StreamConfig extends BaseBean {
    private String defaultDestination = "default-topic";
    private String defaultGroup = "default-group";
    private String defaultContentType = "application/json";
    private Map<String, BindingProperties> bindings = new HashMap<>();

    public String getDefaultDestination() {
        return defaultDestination;
    }

    public void setDefaultDestination(String defaultDestination) {
        this.defaultDestination = defaultDestination;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public String getDefaultContentType() {
        return defaultContentType;
    }

    public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    public Map<String, BindingProperties> getBindings() {
        return bindings;
    }

    public void setBindings(Map<String, BindingProperties> bindings) {
        this.bindings = bindings;
    }
}
