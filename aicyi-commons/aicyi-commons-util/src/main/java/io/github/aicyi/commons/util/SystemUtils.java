package io.github.aicyi.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Mr.Min
 * @description 系统环境工具类
 * @date 2021/4/28
 **/
public final class SystemUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtils.class);

    private SystemUtils() {
    }

    /**
     * 获取本机 IPv4 地址，解析失败返回空串
     */
    public static String getIpAddress() {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.warn("resolve local ip address failed: {}", e.getMessage());
        }
        return "";
    }
}
