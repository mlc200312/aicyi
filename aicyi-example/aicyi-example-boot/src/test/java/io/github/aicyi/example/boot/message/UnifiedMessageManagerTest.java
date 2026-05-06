package io.github.aicyi.example.boot.message;

import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.example.service.channel.MessageChannels;
import io.github.aicyi.midware.message.mail.model.MailMessage;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.mq.model.MqMessage;
import io.github.aicyi.midware.message.core.model.MessageSendResult;
import io.github.aicyi.midware.message.core.sender.MessageSendCallback;
import io.github.aicyi.midware.message.core.sender.UnifiedMessageManager;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.midware.message.sms.model.SmsMessage;
import io.github.aicyi.test.util.DataSource;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
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

    @Before
    @Override
    public void beforeTest() {
    }

    @Test
    @Override
    public void test() {
        SmsMessage smsMessage = SmsMessage.withContent("TEST SEND SMS", Arrays.asList("15910436675"), "TEST");
        smsMessage.setBusinessId(IdUtils.generateV7Id());
        MessageSendResult result = unifiedMessageManager.send(smsMessage);

        log(result);
    }

    @SneakyThrows
    @Test
    public void test2() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        SmsMessage smsMessage = SmsMessage.withContent("TEST SEND SMS", "+8615910436675", "TEST");
        unifiedMessageManager.sendAsync(smsMessage, new MessageSendCallback() {
            @Override
            public void onComplete(MessageSendResult result) {
                log("短信发送完成：" + result);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log("短信发送失败：" + e.getMessage());
            }
        });

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void test3() {
        MqMessage mqMessage = MqMessage.of(DataSource.getUser(), MessageChannels.OUTPUT);
        MessageSendResult result = unifiedMessageManager.send(mqMessage);

        log(result);
    }

    @SneakyThrows
    @Test
    public void test4() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        MqMessage mqMessage = MqMessage.of(DataSource.getUser(), MessageChannels.OUTPUT);
        unifiedMessageManager.sendAsync(mqMessage, new MessageSendCallback() {
            @Override
            public void onComplete(MessageSendResult result) {
                log("消息发送完成：" + result);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log("消息发送失败：" + e.getMessage());
            }
        });

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void test5() {
        MailMessage mailMessage = MailMessage.of("TEST SEND EMAIL", "TEST", "11115910436675@163.com");
        MessageSendResult result = unifiedMessageManager.send(mailMessage);

        log(result);
    }

    @SneakyThrows
    @Test
    public void test6() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        MailMessage mailMessage = MailMessage.of("TEST SEND EMAIL", "TEST", "15910436675@163.com");
        unifiedMessageManager.sendAsync(mailMessage, new MessageSendCallback() {
            @Override
            public void onComplete(MessageSendResult result) {
                log("消息发送完成：" + result);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                log("消息发送失败：" + e.getMessage());
            }
        });

        countDownLatch.await(10, TimeUnit.SECONDS);
    }
}
