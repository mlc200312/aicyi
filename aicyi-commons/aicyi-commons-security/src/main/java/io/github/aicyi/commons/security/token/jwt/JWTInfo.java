package io.github.aicyi.commons.security.token.jwt;

import io.github.aicyi.commons.lang.model.BaseBean;

/**
 * @author Mr.Min
 * @description Jwt信息
 * @date 10:41
 **/
public class JWTInfo extends BaseBean implements IJWTInfo {
    private String id;
    private String uniqueName;
    private String deviceId;

    public JWTInfo() {
    }

    public JWTInfo(String id, String uniqueName, String deviceId) {
        this.id = id;
        this.uniqueName = uniqueName;
        this.deviceId = deviceId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
