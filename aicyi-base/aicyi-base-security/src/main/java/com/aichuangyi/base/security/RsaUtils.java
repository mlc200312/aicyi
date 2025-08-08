package com.aichuangyi.base.security;

import com.aichuangyi.base.lang.Pair;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Mr.Min
 * @description RSA 非对称加密算法
 * @date 2019-05-14
 **/
public class RsaUtils {
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
     * 生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥
     *
     * @return
     */
    public static Pair<byte[], byte[]> generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            return new Pair(publicKey.getEncoded(), privateKey.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
     *
     * @param keyBytes
     * @return
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
     *
     * @param keyBytes
     * @return
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 加密，三步走。
     *
     * @param content
     * @param publicKey
     * @return
     */
    public static String encrypt(String content, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encryptBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException ex) {
            ex.printStackTrace();
        }
        return null;

    }

    /**
     * 解密，三步走。
     *
     * @param content
     * @param privateKey
     * @return
     */
    public static String decrypt(String content, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodeBase64 = Base64.decodeBase64(content.getBytes(StandardCharsets.UTF_8));
            byte[] decryptBytes = cipher.doFinal(decodeBase64);
            return new String(decryptBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(String content, PrivateKey privateKey) throws Exception {
        //根据指定算法获取签名工具
        Signature signature = Signature.getInstance(SHA256_WITH_RSA);
        //用私钥初始化签名工具
        signature.initSign(privateKey);
        //添加要签名的数据
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        //计算签名结果（签名信息）
        byte[] bytes = signature.sign();
        //Base64编码
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 验签
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verify(String content, String sign, PublicKey publicKey) throws Exception {
        //根据指定算法获取签名工具
        Signature signature = Signature.getInstance(SHA256_WITH_RSA);
        //用公钥初始化签名工具
        signature.initVerify(publicKey);
        //添加要校验的数据
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        //Base64解码
        byte[] decodeBase64 = Base64.decodeBase64(sign);
        //校验数据的签名是否正确
        return signature.verify(decodeBase64);
    }

    /**
     * 签名文件
     *
     * @param file
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String signFile(File file, PrivateKey privateKey) throws Exception {
        //根据指定算法获取签名工具
        Signature signature = Signature.getInstance(SHA256_WITH_RSA);
        //用私钥初始化签名工具
        signature.initSign(privateKey);
        getInputStream(file, signature);
        //计算签名结果（签名信息）
        byte[] bytes = signature.sign();
        //Base64编码
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 校验文件
     *
     * @param file
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifyFile(File file, byte[] sign, PublicKey publicKey) throws Exception {
        //根据指定算法获取签名工具
        Signature signature = Signature.getInstance(SHA256_WITH_RSA);
        signature.initVerify(publicKey);
        getInputStream(file, signature);
        //Base64解码
        byte[] decodeBase64 = Base64.decodeBase64(sign);
        //校验数据的签名是否正确
        return signature.verify(decodeBase64);
    }

    private static void getInputStream(File file, Signature signature) throws IOException, SignatureException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                signature.update(buffer, 0, len);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                // 忽略
            }
        }
    }
}