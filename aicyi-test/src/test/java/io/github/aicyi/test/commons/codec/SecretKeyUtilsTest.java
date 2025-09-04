package io.github.aicyi.test.commons.codec;

import io.github.aicyi.commons.core.jwt.SecretKeyUtils;
import io.github.aicyi.test.domain.BaseLoggerTest;
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
        String randomKey = SecretKeyUtils.secretKeyToString(SecretKeyUtils.randomSecretKey());
        SecretKey secretKey = SecretKeyUtils.toHmacSHA256SecretKey(randomKey);
        String keyToString = SecretKeyUtils.secretKeyToString(secretKey);

        assert randomKey.equals(keyToString);

        log("test", randomKey, keyToString);
    }
}
