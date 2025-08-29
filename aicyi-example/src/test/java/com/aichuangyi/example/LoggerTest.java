package com.aichuangyi.example;

import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aichuangyi.commons.logging.LoggerType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 20:36
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class LoggerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerType.BIZ);

    @Test
    public void test() {
        LOGGER.error(new RuntimeException("不支持的操作类型"), "测试失败日志打印");
    }
}
