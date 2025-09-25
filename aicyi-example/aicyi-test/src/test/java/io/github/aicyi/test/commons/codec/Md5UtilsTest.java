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
public class Md5UtilsTest extends BaseLoggerTest {

    @Test
    public void test() {
        String data = "test 123";
        String md5 = Md5Utils.md5(data);
        String generateMd5 = MessageDigestUtils.generateMd5(data);

        assert md5.equals(generateMd5);

        log("test", md5);
    }
}
