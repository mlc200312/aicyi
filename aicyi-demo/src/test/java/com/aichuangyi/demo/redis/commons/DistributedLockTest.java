package com.aichuangyi.demo.redis.commons;

import com.aichuangyi.commons.core.lock.DistributedLock;
import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.test.AicyiFactory;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.util.DataSource;
import com.aicyiframework.redis.lock.RedissonDistributedLock;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiDemoApplication.class)
public class DistributedLockTest extends BaseLoggerTest {

    @Autowired
    private RedissonClient redissonClient;

    private static int LOCK = 3;
    private Queue<DistributedLock> list = new LinkedList<>();

    @Before
    public void before() {
        for (int i = 0; i < LOCK; i++) {
            list.add(new RedissonDistributedLock("myLock:" + i, redissonClient));
        }
    }

    @SneakyThrows
    @Test
    public void test() {

        AicyiFactory factory = getAicyiFactory();

        factory.startRun();

        factory.stopRun();
    }

    private AicyiFactory getAicyiFactory() {

        AicyiFactory factory = new AicyiFactory(500, new AicyiFactory.Project() {
            @Override
            public DistributedLock getLock() {
                return new RedissonDistributedLock("myLock", redissonClient);
//                return null;
            }

            @SneakyThrows
            @Override
            public String work() {
                Thread.sleep(new Random().nextInt(3) * 1000);
                return "I am working...";
            }
        });
        return factory;
    }
}
