package com.aichuangyi.test.commons.security;

import com.aichuangyi.commons.security.SecretKeyUtils;
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
    public void secretKeyUtilsTest() {
        SecretKey secretKey = SecretKeyUtils.randomSecretKey();

        String keyToString = SecretKeyUtils.keyToString(secretKey);

        String randomSecretKeyStr = SecretKeyUtils.randomSecretKeyStr();

        SecretKey stringToKey = SecretKeyUtils.stringToKey(randomSecretKeyStr);

        log("secretKeyUtilsTest", keyToString, randomSecretKeyStr, SecretKeyUtils.keyToString(stringToKey));
    }
}
