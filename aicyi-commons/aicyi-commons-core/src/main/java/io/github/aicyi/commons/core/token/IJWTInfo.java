package io.github.aicyi.commons.core.token;

/**
 * @author Mr.Min
 * @description Jwt信息统一接口
 * @date 15:54
 **/
public interface IJWTInfo {

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    String getId();

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    String getUniqueName();

    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    String getDeviceId();
}
