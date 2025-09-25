package io.github.aicyi.test.commons.codec;

import io.github.aicyi.commons.codec.Md5Utils;
import io.github.aicyi.commons.codec.MessageDigestUtils;
import io.github.aicyi.test.util.BaseLoggerTest;
import org.junit.Test;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:18
 **/
public class MessageDigestUtilsTest extends BaseLoggerTest {

    @Test
    public void test() {
        String data = "test 123";
        String generateMd5 = MessageDigestUtils.generateMd5(data);
        String generateSha1 = MessageDigestUtils.generateSha1(data);
        String generateSha256 = MessageDigestUtils.generateSha256(data);

        assert generateMd5.equals(Md5Utils.md5(data));

        log("test", generateMd5, generateSha1, generateSha256);
    }
}
