package com.aichuangyi.commons.core.token;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Token配置接口
 * @date 2025/8/12
 **/
public interface TokenConfig {

    /**
     * 获取默认Token过期时间
     *
     * @param unit 时间单位
     * @return 过期时间
     */
    long getDefaultExpire(TimeUnit unit);

    /**
     * 获取Token签发者
     */
    String getIssuer();

    /**
     * 获取Token签名密钥
     */
    String getSigningKey();

    /**
     * 获取Token刷新窗口时间
     *
     * @param unit 时间单位
     * @return 窗口时间
     */
    long getRefreshWindow(TimeUnit unit);

    /**
     * 是否允许多Token同时有效
     */
    boolean isMultiTokenAllowed();

    /**
     * 获取Token最大计数
     *
     * @return
     */
    int getMultiTokenCount();
}