package com.aichuangyi.test.commons.codec;

import com.aichuangyi.commons.codec.AesUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
import lombok.SneakyThrows;
import org.junit.Test;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 2021/5/3
 **/
public class AesUtilsTest extends BaseLoggerTest {

    @SneakyThrows
    @Test
    public void test() {
        // AES加密要求key必须要128个比特位（这里需要长度为16，否则会报错）
        String KEY = "1234567887654321";
        String content = "url：findNames.action";

        String encrypt = AesUtils.aesEncrypt(content, KEY);
        String decrypt = AesUtils.aesDecrypt(encrypt, KEY);

        assert decrypt.equals(content);

        log("test", content, KEY, encrypt, decrypt);
    }
}
