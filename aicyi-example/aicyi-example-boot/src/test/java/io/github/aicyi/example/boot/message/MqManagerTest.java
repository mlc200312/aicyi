package io.github.aicyi.example.boot.message;

import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.example.domain.UserBean;
import io.github.aicyi.example.service.channel.MessageChannels;
import io.github.aicyi.midware.rabbitmq.MqManager;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.jsonwebtoken.lang.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 20:30
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class MqManagerTest extends BaseLoggerTest {

    @Autowired(required = false)
    @Qualifier(MessageChannels.OUTPUT)
    private MessageChannel messageChannel;
    @Autowired
    private MqManager mqManager;

    @Override
    public void beforeTest() {

    }

    @Test
    @Override
    public void test() {
        // 方式一
        Message<UserBean> message = MessageBuilder.withPayload(DataSource.getUser()).build();
        boolean send = messageChannel.send(message);
        // 方式一
        boolean send2 = mqManager.send(MessageChannels.OUTPUT, DataSource.getUser());

        log(send, send2);
    }

    @Test
    public void test2() {
        boolean send = mqManager.sendDelayed(MessageChannels.DELAYED_OUTPUT, DataSource.getUser(), 10 * 1000);

        log(send);
    }

    @Test
    public void test3() {
        boolean send = mqManager.send(MessageChannels.DIRECT_OUTPUT, DataSource.getUser());

        log(send);
    }

    @Test
    public void test4() {
        Map<String, Object> properties = Maps.of("routingKey", (Object) "order.created").build();
        boolean send = mqManager.send(MessageChannels.TOPIC_OUTPUT, DataSource.getUser(), properties);

        log(send);
    }

    @Test
    public void test5() {
        Map<String, Object> properties = Maps.of("routingKey", (Object) "order.paid").build();
        boolean send = mqManager.send(MessageChannels.TOPIC_OUTPUT, DataSource.getUser(), properties);

        log(send);
    }

    @Test
    public void test6() {
        Map<String, Object> properties = Maps.of("routingKey", (Object) "system.log").build();
        boolean send = mqManager.send(MessageChannels.TOPIC_OUTPUT, DataSource.getUser(), properties);

        log(send);
    }
}
