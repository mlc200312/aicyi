package io.github.aicyi.test.commons.codec;

import io.github.aicyi.commons.core.jwt.SecretKeyUtils;
import io.github.aicyi.test.util.BaseLoggerTest;
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
        String randomKey = SecretKeyUtils.secretKey2Str(SecretKeyUtils.randomSecretKey());
        SecretKey secretKey = SecretKeyUtils.toHmacSHA256SecretKey(randomKey);
        String secretKey2Str = SecretKeyUtils.secretKey2Str(secretKey);

        assert randomKey.equals(secretKey2Str);

        log("test", randomKey, secretKey2Str);
    }
}
