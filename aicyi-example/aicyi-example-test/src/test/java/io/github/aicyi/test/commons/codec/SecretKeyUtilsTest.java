package io.github.aicyi.test.commons.codec;

import io.github.aicyi.commons.security.SecretKeyUtils;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.Test;

import javax.crypto.SecretKey;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:14
 **/
public class SecretKeyUtilsTest extends BaseLoggerTest {

    @Override
    public void beforeTest() {

    }

    @Test
    public void test() {
        SecretKey secretKeyFor = Keys.secretKeyFor(SignatureAlgorithm.HS256);

//        String base64Key = IdGenerator.generateV7Id();

//        SecretKey secretKeyFor = SecretKeyUtils.toHmacSHA256SecretKey(base64Key);

        String secretKey2String = SecretKeyUtils.asString(secretKeyFor);
        SecretKey secretKey = SecretKeyUtils.toSecretKeyForHmacSHA256(secretKey2String);
        String secretKey2Str = SecretKeyUtils.asString(secretKey);
        assert secretKey2String.equals(secretKey2Str);

        log(secretKey2String, secretKey2Str);
    }
}
