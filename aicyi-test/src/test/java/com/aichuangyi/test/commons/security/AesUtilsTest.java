package com.aichuangyi.test.commons.security;

import com.aichuangyi.commons.security.AesUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
import org.junit.Test;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 2021/5/3
 **/
public class AesUtilsTest extends BaseLoggerTest {

    @Test
    public void aesTest() {
        try {
            // AES加密要求key必须要128个比特位（这里需要长度为16，否则会报错）
            String KEY = "1234567887654321";
            String content = "url：findNames.action";
            System.out.println("加密前：" + content);
            System.out.println("加密密钥和解密密钥：" + KEY);

            String encrypt = AesUtils.aesEncrypt(content, KEY);
            System.out.println("加密后：" + encrypt);

            String decrypt = AesUtils.aesDecrypt(encrypt, KEY);
            System.out.println("解密后：" + decrypt);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
