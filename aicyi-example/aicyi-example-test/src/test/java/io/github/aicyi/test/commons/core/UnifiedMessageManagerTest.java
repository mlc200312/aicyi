package io.github.aicyi.test.commons.core;

import io.github.aicyi.commons.core.message.*;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.midware.kit.TwilioSmsManager;
import io.github.aicyi.midware.message.sms.SmsManager;
import io.github.aicyi.midware.message.sms.SmsMessageSender;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.midware.message.sms.SmsMessage;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:12
 **/
public class UnifiedMessageManagerTest extends BaseLoggerTest {

    private UnifiedMessageManager unifiedMessageManager;

    @Before
    @Override
    public void beforeTest() {

        SmsManager smsManager = new TwilioSmsManager("AC56dfa4c2d3284693ddebd2834b499486", "eaee6879fd36fbe39ed60c49cff457e0", "+19472182422", new HashMap<>());

        MessageSenderFactory factory = new DefaultMessageSenderFactory();
        Optional.ofNullable(smsManager).ifPresent(item -> factory.registerSender(MessageType.SMS, new SmsMessageSender(smsManager)));

        // 创建统一消息服务
        unifiedMessageManager = new DefaultUnifiedMessageManager(factory);
    }

    @SneakyThrows
    @Test
    @Override
    public void test() {
        SmsMessage smsMessage = SmsMessage.withContent("TEST SEND SMS", Arrays.asList("15910436675"), "TEST");
        smsMessage.setBusinessId(IdUtils.generateV7Id());
        SendResult result = unifiedMessageManager.send(smsMessage);

        log(result);
    }

    @SneakyThrows
    @Test
    public void test2() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        SmsMessage smsMessage = SmsMessage.of("test_template", "+8615910436675", new HashMap<>(), "TEST");
        unifiedMessageManager.sendAsync(smsMessage, new SendCallback() {
            @Override
            public void onComplete(SendResult result) {
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
}
