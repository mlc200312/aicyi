package com.aichuangyi.demo.core;

import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aicyiframework.core.message.*;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @SneakyThrows
    @Test
    @Override
    public void test() {
        EmailMessage emailMessage = EmailMessage.of("test send email", "Send Email", "15910436675@163.com");
        SendResult result = unifiedMessageManager.send(emailMessage);

        assert result.isSuccess();

        log("test", result);
    }

    @SneakyThrows
    @Test
    public void test2() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        EmailMessage asyncEmailMessage = EmailMessage.of("test send async email", "Send Async Email", "1591043667@163.com");
        unifiedMessageManager.sendAsync(asyncEmailMessage, new SendCallback() {
            @Override
            public void onComplete(SendResult result) {
                log("test2", "邮件发送完成：" + result);

                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log("test2", "邮件发送失败：" + e.getMessage());
            }
        });

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void test3() {
        SmsMessage smsMessage = SmsMessage.withContent("tes send sms", Arrays.asList("+8615910436675"), "TEST");
        SendResult result = unifiedMessageManager.send(smsMessage);

        log("test3", result);
    }

    @SneakyThrows
    @Test
    public void test4() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        SmsMessage smsMessage = SmsMessage.of("test_template", "+8615910436675", new HashMap<>(), "TEST");
        unifiedMessageManager.sendAsync(smsMessage, new SendCallback() {
            @Override
            public void onComplete(SendResult result) {
                log("test4", "短信发送完成：" + result);

                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log("test4", "短信发送失败：" + e.getMessage());
            }
        });

        countDownLatch.await(10, TimeUnit.SECONDS);
    }
}
