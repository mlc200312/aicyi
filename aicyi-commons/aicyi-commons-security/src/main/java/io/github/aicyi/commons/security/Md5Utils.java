package io.github.aicyi.commons.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Mr.Min
 * @description Md5工具类，能力与 {@link MessageDigestUtils#generateMd5(String)} 重复，
 * 新代码请使用 MessageDigestUtils
 * @date 16:13
 **/
public final class Md5Utils {

    private Md5Utils() {
    }

    /**
     * md5
     *
     * @param data 待摘要的字符串
     * @return 32 位小写十六进制摘要
     * @deprecated 与 {@link MessageDigestUtils#generateMd5(String)} 重复，请使用后者
     */
    @Deprecated
    public static String md5(String data) {
        return DigestUtils.md5Hex(data);
    }
}
