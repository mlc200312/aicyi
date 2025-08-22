package com.aichuangyi.test.commons.codec;

import com.aichuangyi.commons.lang.Pair;
import com.aichuangyi.commons.codec.RsaUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-05-21
 **/
public class RsaUtilsTest extends BaseLoggerTest {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Before
    public void beforeTest() {
        Pair<byte[], byte[]> keyPair = RsaUtils.generateKeyPair();
        publicKey = RsaUtils.restorePublicKey(keyPair.getKey());
        privateKey = RsaUtils.restorePrivateKey(keyPair.getValue());
    }

    @Test
    public void test() {
        // 加密
        String text = "test rsa encrypt";
        String encrypt = RsaUtils.encrypt(text, publicKey);
        String str = "RSA encoded: " + encrypt;

        // 解密
        String decrypt = RsaUtils.decrypt(encrypt, privateKey);
        String str2 = "RSA decoded: " + decrypt;

        assert text.equals(decrypt);

        log("test", str, str2);
    }

    @Test
    public void signTest() throws Exception {
        //签名
        String signContent = "test rsa sign";
        String sign = RsaUtils.sign(signContent, privateKey);

        //验签
        boolean verify = RsaUtils.verify(signContent, sign, publicKey);

        assert verify;

        log("signTest", sign, verify);
    }
}
