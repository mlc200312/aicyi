package io.github.aicyi.example.boot.redis;

import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.commons.core.IdGenerator;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import io.github.aicyi.midware.redis.id.RedisSnowflakeIdGenerator;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:45
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class RedisSnowflakeIdGeneratorTest extends BaseLoggerTest {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private RedisTemplate<String, String> stringRedisTemplate;

    @Before
    public void before() {
        this.stringRedisTemplate = new EnhancedRedisTemplateFactory(redisConnectionFactory).getStringTemplate();
    }

    @Override
    public void test() {
        IdGenerator idGenerator = new RedisSnowflakeIdGenerator(stringRedisTemplate);

        for (int i = 0; i < 50; i++) {

            long id = idGenerator.nextId();

            System.out.println(id);
        }
    }
}
