package io.github.aicyi.commons.codec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Mr.Min
 * @description 密钥工具类
 * @date 17:10
 **/
public class SecretKeyUtils {

    /**
     * 将SecretKey转换为Base64字符串
     *
     * @param secretKey
     * @return
     */
    public static String asString(SecretKey secretKey) {
        byte[] encoded = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(encoded);
    }

    /**
     * 将Base64字符串转换为SecretKey
     *
     * @param base64Key
     * @return
     */
    public static SecretKey toSecretKey(String base64Key, String algorithm) {
        // 解码Base64字符串
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        // 创建SecretKeySpec对象，指定算法为HmacSHA256
        return new SecretKeySpec(keyBytes, algorithm);
    }

    /**
     * 将Base64字符串转换为SecretKey
     *
     * @param base64Key
     * @return
     */
    public static SecretKey toSecretKeyForHmacSHA256(String base64Key) {
        return toSecretKey(base64Key, "HmacSHA256");
    }
}
