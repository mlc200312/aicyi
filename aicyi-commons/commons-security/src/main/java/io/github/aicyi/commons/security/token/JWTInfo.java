package io.github.aicyi.commons.security.token;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.core.IJWTInfo;

import java.util.Objects;

/**
 * @author Mr.Min
 * @description Jwt信息
 * @date 10:41
 **/
public class JWTInfo extends BaseBean implements IJWTInfo {
    private String id;
    private String uniqueName;
    private String deviceId;
    private boolean isMainDevice;

    public JWTInfo() {
    }

    public JWTInfo(String id, String uniqueName, String deviceId) {
        this.id = id;
        this.uniqueName = uniqueName;
        this.deviceId = deviceId;
        this.isMainDevice = true;
    }

    public JWTInfo(String id, String uniqueName, String deviceId, boolean isMainDevice) {
        this.id = id;
        this.uniqueName = uniqueName;
        this.deviceId = deviceId;
        this.isMainDevice = isMainDevice;
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

    @Override
    public boolean isMainDevice() {
        return isMainDevice;
    }

    public void setMainDevice(boolean mainDevice) {
        isMainDevice = mainDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JWTInfo jwtInfo = (JWTInfo) o;
        return Objects.equals(id, jwtInfo.id) && Objects.equals(uniqueName, jwtInfo.uniqueName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, uniqueName);
    }
}
