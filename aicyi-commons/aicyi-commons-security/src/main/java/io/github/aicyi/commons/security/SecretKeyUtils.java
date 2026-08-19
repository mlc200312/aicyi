package io.github.aicyi.commons.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Mr.Min
 * @description 密钥工具类
 * @date 17:10
 **/
public final class SecretKeyUtils {

    private SecretKeyUtils() {
    }

    /**
     * 将SecretKey转换为Base64字符串
     *
     * @param secretKey 密钥
     * @return Base64 编码字符串
     */
    public static String asString(SecretKey secretKey) {
        byte[] encoded = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(encoded);
    }

    /**
     * 将Base64字符串转换为SecretKey
     *
     * @param base64Key Base64 编码密钥
     * @param algorithm 密钥算法
     * @return SecretKey
     */
    public static SecretKey toSecretKey(String base64Key, String algorithm) {
        // 解码Base64字符串
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(keyBytes, algorithm);
    }

    /**
     * 将Base64字符串转换为HmacSHA256 SecretKey
     *
     * @param base64Key Base64 编码密钥
     * @return SecretKey
     */
    public static SecretKey toSecretKeyForHmacSHA256(String base64Key) {
        return toSecretKey(base64Key, "HmacSHA256");
    }
}
