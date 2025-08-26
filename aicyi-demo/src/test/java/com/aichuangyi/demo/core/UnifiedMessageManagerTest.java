package com.aichuangyi.demo.core;

import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aicyiframework.core.message.EmailMessage;
import com.aicyiframework.core.message.UnifiedMessageManager;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:12
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiDemoApplication.class)
public class UnifiedMessageManagerTest extends BaseLoggerTest {

    @Autowired
    private UnifiedMessageManager unifiedMessageManager;

    @Override
    public void test() {
        EmailMessage message = EmailMessage.of("test send email", "Send EMAIL", "15910436675@163.com");
        unifiedMessageManager.send(message);
    }
}
