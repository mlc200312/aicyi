package com.aichuangyi.test.base.security;

import com.aichuangyi.base.security.Md5Utils;
import com.aichuangyi.base.security.MessageDigestUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
import org.junit.Test;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:18
 **/
public class MessageDigestUtilsTest extends BaseLoggerTest {

    @Test
    public void digestTest() {
        String data = "test 123";
        String generateMd5 = MessageDigestUtils.generateMd5(data);
        String generateSha1 = MessageDigestUtils.generateSha1(data);
        String generateSha256 = MessageDigestUtils.generateSha256(data);

        log("digestTest", generateMd5, generateSha1, generateSha256, generateMd5.equals(Md5Utils.md5(data)));
    }
}
