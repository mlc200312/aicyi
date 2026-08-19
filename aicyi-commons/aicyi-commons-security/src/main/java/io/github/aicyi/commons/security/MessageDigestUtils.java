package io.github.aicyi.commons.security;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author Mr.Min
 * @description SHA1、SHA-256、MD5 工具类
 * @date 2025/8/8
 **/
public final class MessageDigestUtils {

    private MessageDigestUtils() {
    }
    public static String generateMd5(String str) {
        return generateMd5(getBytes(str));
    }

    public static String generateMd5(byte[] input) {
        return Hex.encodeHexString(DigestUtils.getMd5Digest().digest(input));
    }

    public static String generateSha1(String str) {
        return generateSha1(getBytes(str));
    }

    public static String generateSha1(byte[] input) {
        return Hex.encodeHexString(DigestUtils.getSha1Digest().digest(input));
    }

    public static String generateSha256(String str) {
        return generateSha256(getBytes(str));
    }

    public static String generateSha256(byte[] input) {
        return Hex.encodeHexString(DigestUtils.getSha256Digest().digest(input));
    }

    private static byte[] getBytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }
}