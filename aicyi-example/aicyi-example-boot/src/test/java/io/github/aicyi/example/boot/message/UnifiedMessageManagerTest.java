package io.github.aicyi.example.boot.message;

import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.example.channel.MessageChannels;
import io.github.aicyi.midware.rabbitmq.MqMessage;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.commons.core.message.SendCallback;
import io.github.aicyi.commons.core.message.SendResult;
import io.github.aicyi.commons.core.message.UnifiedMessageManager;
import io.github.aicyi.midware.message.mail.EmailMessage;
import io.github.aicyi.midware.message.sms.SmsMessage;
import io.github.aicyi.test.util.DataSource;
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
@SpringBootTest(classes = AicyiExampleApplication.class)
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
    public void sendEmailErrorTest() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        EmailMessage asyncEmailMessage = EmailMessage.of("test send async email", "Send Async Email", "1591043667@163.com");
        unifiedMessageManager.sendAsync(asyncEmailMessage, new SendCallback() {
            @Override
            public void onComplete(SendResult result) {
                log("sendAsyncEmailTest", "邮件发送完成：" + result);

                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log("sendAsyncEmailTest", "邮件发送失败：" + e.getMessage());
            }
        });

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void sendSmsTest() {
        SmsMessage smsMessage = SmsMessage.withContent("tes send sms", Arrays.asList("+8615910436675"), "TEST");
        SendResult result = unifiedMessageManager.send(smsMessage);

        log("sendSmsTest", result);
    }

    @SneakyThrows
    @Test
    public void sendSmsErrorTest() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        SmsMessage smsMessage = SmsMessage.of("test_template", "+8615910436675", new HashMap<>(), "TEST");
        unifiedMessageManager.sendAsync(smsMessage, new SendCallback() {
            @Override
            public void onComplete(SendResult result) {
                log("sendTemplateSmsTest", "短信发送完成：" + result);

                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log("sendTemplateSmsTest", "短信发送失败：" + e.getMessage());
            }
        });

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void sendMqTest() {
        MqMessage mqMessage = MqMessage.builder()
                .destination(MessageChannels.OUTPUT)
                .content(DataSource.getUser())
                .build();
        SendResult result = unifiedMessageManager.send(mqMessage);

        log("sendMqTest", result);
    }
}
