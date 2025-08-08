package com.aichuangyi.demo.logging;

import com.aichuangyi.base.logging.Logger;
import com.aichuangyi.base.logging.LoggerFactory;
import com.aichuangyi.base.logging.LoggerType;
import com.aichuangyi.demo.AicyiDemoApplication;
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
@SpringBootTest(classes = AicyiDemoApplication.class)
public class LoggerTest {
    private static final Logger logger = LoggerFactory.getLogger(LoggerType.BIZ);

    @Test
    public void loggerTest() {
        logger.info("loggerTest", new RuntimeException("loggerTest"));
    }
}
