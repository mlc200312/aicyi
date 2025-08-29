package com.aichuangyi.example.core;

import com.aichuangyi.example.AicyiExampleApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.domain.User;
import com.aichuangyi.test.util.DataSource;
import com.aicyiframework.core.message.SendResult;
import com.aicyiframework.core.mq.MqManager;
import com.aichuangyi.example.channel.MessageChannels;
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
