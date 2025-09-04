package io.github.aicyi.example.message;

import io.github.aicyi.example.AicyiExampleApplication;
import io.github.aicyi.test.domain.BaseLoggerTest;
import io.github.aicyi.test.domain.User;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.commons.core.message.SendResult;
import io.github.aicyi.example.channel.MessageChannels;
import io.github.aicyi.midware.rabbitmq.MqManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 20:30
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class MqManagerTest extends BaseLoggerTest {

    @Autowired
    private MqManager mqManager;
    @Autowired(required = false)
    @Qualifier(MessageChannels.OUTPUT)
    private MessageChannel messageChannel;


    @Test
    @Override
    public void test() {
        SendResult send = mqManager.send(DataSource.getUser(), "default.exchange", "default.queue");

        log("test", send);
    }

    @Test
    public void test2() {
        Message<User> message = MessageBuilder.withPayload(DataSource.getUser()).build();
        boolean send = messageChannel.send(message);

        log("test", send);
    }
}
