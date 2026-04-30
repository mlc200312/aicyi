package io.github.aicyi.example.boot.message;

import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.midware.message.sms.SmsManager;
import io.github.aicyi.test.util.BaseLoggerTest;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:41
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class SmsManagerTest extends BaseLoggerTest {

    @Autowired
    private SmsManager smsManager;

    private List<String> numbers;
    private String content;
    private String signName;

    @Before
    @Override
    public void beforeTest() {
        numbers = Arrays.asList("15910436675", "13661371201");
        content = "测试短信";
        signName = "测试";
    }

    @Test
    @Override
    public void test() {

        boolean isTrue = smsManager.sendTextSms(numbers.get(0), content, signName);

        log(isTrue);
    }

    @SneakyThrows
    @Test
    public void test2() {

        CompletableFuture<Boolean> async = smsManager.sendTextSmsAsync(numbers, content, signName);

        Boolean isTrue = async.get();

        log(isTrue);
    }

    @Test
    public void test3() {

        boolean isTrue = smsManager.sendTemplateSms(numbers.get(0), "SMS_168900000", null, signName);

        log(isTrue);
    }

    @SneakyThrows
    @Test
    public void test4() {

        CompletableFuture<Boolean> async = smsManager.sendTemplateSmsAsync(numbers, "SMS_168900000", null, signName);

        Boolean isTrue = async.get();

        log(isTrue);
    }
}
