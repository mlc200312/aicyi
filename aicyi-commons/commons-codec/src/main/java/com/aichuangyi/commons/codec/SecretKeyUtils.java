package com.aichuangyi.commons.codec;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
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
    public static String keyToString(SecretKey secretKey) {
        byte[] encodedKey = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    /**
     * 将Base64字符串转换为SecretKey
     *
     * @param encodedKey
     * @return
     */
    public static SecretKey stringToKey(String encodedKey) {
        return Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    /**
     * 随机生成Key
     *
     * @return
     */
    public static SecretKey randomSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    /**
     * 随机生成Key
     *
     * @return
     */
    public static String randomSecretKeyStr() {
        return keyToString(randomSecretKey());
    }
}
