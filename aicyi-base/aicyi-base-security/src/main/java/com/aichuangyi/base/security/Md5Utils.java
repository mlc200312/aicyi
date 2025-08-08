package com.aichuangyi.base.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Mr.Min
 * @description Md5工具类
 * @date 16:13
 **/
public class Md5Utils {

    /**
     * md5
     *
     * @param data
     * @return
     */
    public static String md5(String data) {
        return DigestUtils.md5Hex(data);
    }
}
