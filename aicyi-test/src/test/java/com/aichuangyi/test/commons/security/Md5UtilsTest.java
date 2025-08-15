package com.aichuangyi.test.commons.security;

import com.aichuangyi.commons.security.Md5Utils;
import com.aichuangyi.commons.security.MessageDigestUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
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

        log("test", md5, md5.equals(generateMd5));
    }
}
