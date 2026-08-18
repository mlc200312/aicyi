package io.github.aicyi.commons.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @author Mr.Min
 * @description AES 加解密工具（AES/GCM/NoPadding）
 * <p>
 * 加密输出格式为 Base64(IV || 密文)，IV 为每次加密随机生成的 12 字节；
 * 密钥必须为 16/24/32 字节（UTF-8 编码后长度）
 * @date 2021/5/5
 **/
public final class AesUtils {

    private AesUtils() {
    }

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * GCM 模式（认证加密，替代已废弃的 ECB 模式）
     */
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    /**
     * GCM IV 长度（字节）
     */
    private static final int GCM_IV_LENGTH = 12;
    /**
     * GCM 认证标签长度（比特）
     */
    private static final int GCM_TAG_LENGTH = 128;
    /**
     * 随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * AES 加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥（UTF-8 编码后长度必须为 16/24/32 字节）
     * @return 加密后的 Base64 字符串（IV || 密文）；入参为空白时原样返回
     */
    public static String aesEncrypt(String content, String encryptKey) {
        if (content == null || content.trim().isEmpty()) {
            return content;
        }
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, buildSecretKey(encryptKey), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] cipherBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

            byte[] result = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(cipherBytes, 0, result, iv.length, cipherBytes.length);
            return Base64.encodeBase64String(result);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("AES encrypt failed", e);
        }
    }

    /**
     * AES 解密
     *
     * @param encryptStr 待解密的 Base64 字符串（IV || 密文）
     * @param decryptKey 解密密钥（UTF-8 编码后长度必须为 16/24/32 字节）
     * @return 解密后的明文；入参为空白时原样返回
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) {
        if (encryptStr == null || encryptStr.trim().isEmpty()) {
            return encryptStr;
        }
        try {
            byte[] decoded = Base64.decodeBase64(encryptStr);
            if (decoded.length <= GCM_IV_LENGTH) {
                throw new IllegalArgumentException("invalid AES cipher text length: " + decoded.length);
            }
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, GCM_IV_LENGTH);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, buildSecretKey(decryptKey), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] plainBytes = cipher.doFinal(decoded, GCM_IV_LENGTH, decoded.length - GCM_IV_LENGTH);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("AES decrypt failed", e);
        }
    }

    /**
     * 构建 AES 密钥，校验密钥长度必须为 16/24/32 字节
     */
    private static SecretKeySpec buildSecretKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("AES key may not be null");
        }
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key length must be 16/24/32 bytes, actual: " + keyBytes.length);
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
