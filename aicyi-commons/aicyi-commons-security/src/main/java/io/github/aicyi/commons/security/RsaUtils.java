package io.github.aicyi.commons.security;

import io.github.aicyi.commons.lang.model.Pair;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Mr.Min
 * @description RSA 非对称加密算法
 * @date 2019-05-14
 **/
public final class RsaUtils {

    private RsaUtils() {
    }

    /**
     * RSA算法
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * RSA密钥长度必须是64的倍数，在512~65536之间。默认是2048
     */
    public static final int KEY_SIZE = 2048;
    /**
     * 加密方式，1、RSA/None/PKCS1Padding 2、RSA/ECB/PKCS1Padding
     */
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    /**
     * 签名方式，1.SHA256WithRSA 2.SHA1WithRSA
     */
    public static final String SHA256_WITH_RSA = "SHA256WithRSA";

    /**
     * 生成密钥对，返回 Pair(公钥编码, 私钥编码)
     */
    public static Pair<byte[], byte[]> generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            return new Pair<>(publicKey.getEncoded(), privateKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("generate RSA key pair failed", e);
        }
    }

    /**
     * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("restore RSA public key failed", e);
        }
    }

    /**
     * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("restore RSA private key failed", e);
        }
    }

    /**
     * 公钥加密，返回 Base64 密文
     */
    public static String encrypt(String content, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encryptBytes);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("RSA encrypt failed", e);
        }
    }

    /**
     * 私钥解密，入参为 Base64 密文
     */
    public static String decrypt(String content, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodeBase64 = Base64.decodeBase64(content.getBytes(StandardCharsets.UTF_8));
            byte[] decryptBytes = cipher.doFinal(decodeBase64);
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("RSA decrypt failed", e);
        }
    }

    /**
     * 私钥签名，返回 Base64 签名值
     */
    public static String sign(String content, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SHA256_WITH_RSA);
            signature.initSign(privateKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(signature.sign());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("RSA sign failed", e);
        }
    }

    /**
     * 公钥验签
     */
    public static boolean verify(String content, String sign, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SHA256_WITH_RSA);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("RSA verify failed", e);
        }
    }

    /**
     * 文件签名，返回 Base64 签名值
     */
    public static String signFile(File file, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SHA256_WITH_RSA);
            signature.initSign(privateKey);
            updateSignatureFromFile(file, signature);
            return Base64.encodeBase64String(signature.sign());
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("RSA sign file failed", e);
        }
    }

    /**
     * 文件验签
     */
    public static boolean verifyFile(File file, byte[] sign, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SHA256_WITH_RSA);
            signature.initVerify(publicKey);
            updateSignatureFromFile(file, signature);
            return signature.verify(Base64.decodeBase64(sign));
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("RSA verify file failed", e);
        }
    }

    /**
     * 流式读取文件内容并更新到签名对象，避免大文件整体载入内存
     */
    private static void updateSignatureFromFile(File file, Signature signature) throws IOException, SignatureException {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                signature.update(buffer, 0, len);
            }
        }
    }
}
