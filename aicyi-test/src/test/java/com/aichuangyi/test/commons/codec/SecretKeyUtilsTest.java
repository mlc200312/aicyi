package com.aichuangyi.test.commons.codec;

import com.aichuangyi.commons.codec.SecretKeyUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
import org.junit.Test;

import javax.crypto.SecretKey;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:14
 **/
public class SecretKeyUtilsTest extends BaseLoggerTest {

    @Test
    public void test() {
        String randomSecretKeyStr = SecretKeyUtils.randomSecretKeyStr();

        SecretKey secretKey = SecretKeyUtils.stringToKey(randomSecretKeyStr);

        String keyToString = SecretKeyUtils.keyToString(secretKey);

        assert randomSecretKeyStr.equals(keyToString);

        log("test", randomSecretKeyStr, keyToString);
    }
}
