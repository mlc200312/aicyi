package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description Jwt信息统一接口
 * @date 15:54
 **/
public interface IJWTInfo {

    /**
     * 获取用户ID
     *
     * @return
     */
    String getId();

    /**
     * 获取用户名
     *
     * @return
     */
    String getUniqueName();

    /**
     * tokenId
     *
     * @return
     */
    String getTokenId();

    /**
     * 设备ID
     *
     * @return
     */
    String getDeviceId();

    /**
     * 是否为主设备
     *
     * @return
     */
    boolean isMainDevice();
}
