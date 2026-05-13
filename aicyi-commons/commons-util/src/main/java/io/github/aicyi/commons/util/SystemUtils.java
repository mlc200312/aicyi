package io.github.aicyi.commons.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 2021/4/28
 **/
public class SystemUtils {

    public static String getIpAddress() {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }
}
