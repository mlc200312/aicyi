package io.github.aicyi.commons.core.jwt;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.lang.IJWTInfo;
import io.github.aicyi.commons.util.id.IdGenerator;

/**
 * @author Mr.Min
 * @description Jwt信息
 * @date 10:41
 **/
public class JWTInfo extends BaseBean implements IJWTInfo {
    private String id;
    private String uniqueName;
    private String tokenId;
    private String deviceId;
    private boolean isMainDevice;


    public JWTInfo() {
    }

    public JWTInfo(String id, String uniqueName, String deviceId) {
        this.id = id;
        this.uniqueName = uniqueName;
        this.tokenId = IdGenerator.generateV7Id();
        this.deviceId = deviceId;
        this.isMainDevice = true;
    }

    public JWTInfo(String id, String uniqueName, String deviceId, boolean isMainDevice) {
        this.id = id;
        this.uniqueName = uniqueName;
        this.tokenId = IdGenerator.generateV7Id();
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
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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
}
