package com.aichuangyi.commons.lang;

/**
 * @author Mr.Min
 * @description 通用用户信息
 * @date 10:41
 **/
public class UserInfo extends BaseBean {
    private String userId;
    private String username;
    private String deviceId;
    private boolean isMasterDevice;

    public UserInfo() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isMasterDevice() {
        return isMasterDevice;
    }

    public void setMasterDevice(boolean masterDevice) {
        isMasterDevice = masterDevice;
    }

    public static UserInfoBuilder builder() {
        return new UserInfoBuilder();
    }

    public static class UserInfoBuilder {
        private String userId;
        private String username;
        private String deviceId;
        private boolean isMasterDevice;

        private UserInfoBuilder() {
        }

        public UserInfoBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public UserInfoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserInfoBuilder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public UserInfoBuilder isMasterDevice(boolean isMasterDevice) {
            this.isMasterDevice = isMasterDevice;
            return this;
        }

        public UserInfo build() {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setDeviceId(deviceId);
            userInfo.setUsername(username);
            userInfo.setMasterDevice(isMasterDevice);
            return userInfo;
        }
    }
}
