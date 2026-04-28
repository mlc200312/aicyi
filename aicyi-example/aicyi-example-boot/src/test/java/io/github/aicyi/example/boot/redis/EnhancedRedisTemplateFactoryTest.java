package io.github.aicyi.example.boot.redis;

import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;
import io.github.aicyi.test.domain.Example;
import io.github.aicyi.test.domain.ExampleBean;
import io.github.aicyi.test.domain.Message;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import com.fasterxml.jackson.databind.JavaType;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 12:07
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class EnhancedRedisTemplateFactoryTest extends BaseLoggerTest {

    @Autowired
    private EnhancedRedisTemplateFactory enhancedRedisTemplateFactory;

    private RedisTemplate<String, String> stringTemplate;
    private RedisTemplate<String, ExampleBean> exampleRedisTemplate;

    @Before
    @Override
    public void beforeTest() {
        stringTemplate = enhancedRedisTemplateFactory.getStringTemplate();
        exampleRedisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(JacksonTypeFactory.typeOf(ExampleBean.class));
    }

    @Test
    public void test() {
        BoundValueOperations<String, String> stringRedisTemplate = stringTemplate.boundValueOps("stringRedisTemplateTest");
        stringRedisTemplate.set("stringRedisTemplateTest");
        String value = stringRedisTemplate.get();
        assert value.equals("stringRedisTemplateTest");

        BoundValueOperations<String, String> intRedisTemplate = stringTemplate.boundValueOps("intRedisTemplateTest");
        intRedisTemplate.set("999999");
        intRedisTemplate.increment();
        String value2 = intRedisTemplate.get();
        assert value2.equals("1000000");

        log(value, value2);
    }

    @Test
    public void jsonRedisTemplateTest() {
        BoundValueOperations<String, ExampleBean> redisTemplateTest = exampleRedisTemplate.boundValueOps("jsonRedisTemplateTest");
        redisTemplateTest.set(DataSource.getExample());
        ExampleBean example = redisTemplateTest.get();
        String exampleStr = stringTemplate.opsForValue().get("jsonRedisTemplateTest");
        assert example != null;

        JavaType exampleType = JacksonTypeFactory.parametricTypeOf(List.class, JacksonTypeFactory.typeOf(Example.class));
        RedisTemplate<String, List<ExampleBean>> listRedisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(exampleType);
        BoundValueOperations<String, List<ExampleBean>> jsonListRedisTemplateTest = listRedisTemplate.boundValueOps("jsonListRedisTemplateTest");
        jsonListRedisTemplateTest.set(DataSource.getExampleList());
        List<ExampleBean> exampleList = jsonListRedisTemplateTest.get();
        String exampleListStr = stringTemplate.opsForValue().get("jsonListRedisTemplateTest");
        assert CollectionUtils.isNotEmpty(exampleList) && exampleList.size() == DataSource.MAX_NUM;

        log(example, exampleStr, exampleList, exampleListStr);
    }

    @Test
    public void jsonStrRedisTemplateTest() {
        JavaType stringType = JacksonTypeFactory.typeOf(String.class);
        RedisTemplate<String, String> redisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(stringType);
        BoundValueOperations<String, String> jsonStrRedisTemplateTest = redisTemplate.boundValueOps("jsonStrRedisTemplateTest");
        jsonStrRedisTemplateTest.set("jsonStrRedisTemplateTest");
        String value = jsonStrRedisTemplateTest.get();
        String valueStr = stringTemplate.opsForValue().get("jsonStrRedisTemplateTest");
        assert value.equals("jsonStrRedisTemplateTest");

        log(value, valueStr);
    }

    @Test
    public void jsonIntRedisTemplateTest() {
        JavaType intType = JacksonTypeFactory.typeOf(Integer.class);
        RedisTemplate<String, Integer> redisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(intType);
        BoundValueOperations<String, Integer> jsonIntRedisTemplateTest = redisTemplate.boundValueOps("jsonIntRedisTemplateTest");
        jsonIntRedisTemplateTest.set(999999);
        Integer value = jsonIntRedisTemplateTest.get();
        jsonIntRedisTemplateTest.increment();
        String valueStr = stringTemplate.opsForValue().get("jsonIntRedisTemplateTest");
        assert value.equals(999999);

        log(value, valueStr);
    }

    @Test
    public void objectJdkRedisTemplateTest() {
        RedisTemplate<String, Object> genericTemplate = enhancedRedisTemplateFactory.getGenericTemplate(EnhancedRedisTemplateFactory.SerializerType.JDK);
        BoundValueOperations<String, Object> objectJdkRedisTemplateTest = genericTemplate.boundValueOps("objectJdkRedisTemplateTest");
        objectJdkRedisTemplateTest.set(DataSource.getExample());
        Object value = objectJdkRedisTemplateTest.get();
        String valueStr = stringTemplate.opsForValue().get("objectJdkRedisTemplateTest");
        assert value instanceof ExampleBean && value != null;

        log(value, valueStr);
    }


    @Test
    public void objectXmlRedisTemplateTest() {
        RedisTemplate<String, Message> genericTemplate = enhancedRedisTemplateFactory.getXmlTemplate(Message.class);
        BoundValueOperations<String, Message> objectXmlRedisTemplateTest = genericTemplate.boundValueOps("objectXmlRedisTemplateTest");
        objectXmlRedisTemplateTest.set(DataSource.getMessage());
        Message value = objectXmlRedisTemplateTest.get();
        String valueStr = stringTemplate.opsForValue().get("objectXmlRedisTemplateTest");
        assert value != null && value instanceof Message;

        log(value, valueStr);
    }

    @Test
    public void objectJsonRedisTemplateTest() {
        RedisTemplate<String, Object> genericTemplate = enhancedRedisTemplateFactory.getGenericTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON);
        BoundValueOperations<String, Object> objectJsonRedisTemplateTest = genericTemplate.boundValueOps("objectJsonRedisTemplateTest");
        objectJsonRedisTemplateTest.set(DataSource.getExample());
        Object value = objectJsonRedisTemplateTest.get();
        String valueStr = stringTemplate.opsForValue().get("objectJsonRedisTemplateTest");
        assert value != null;

        log(value, valueStr);
    }

    @Test
    public void objectStrJsonRedisTemplateTest() {
        RedisTemplate<String, Object> genericTemplate = enhancedRedisTemplateFactory.getGenericTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON);
        BoundValueOperations<String, Object> objectStrJsonRedisTemplateTest = genericTemplate.boundValueOps("objectStrJsonRedisTemplateTest");
        objectStrJsonRedisTemplateTest.set("objectStrJsonRedisTemplateTest");
        Object value = objectStrJsonRedisTemplateTest.get();
        String valueStr = stringTemplate.opsForValue().get("objectStrJsonRedisTemplateTest");
        assert value instanceof String;

        log(value, valueStr);
    }

    @Test
    public void objectNumJsonRedisTemplateTest() {
        RedisTemplate<String, Object> genericTemplate = enhancedRedisTemplateFactory.getGenericTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON);
        BoundValueOperations<String, Object> objectNumJsonRedisTemplateTest = genericTemplate.boundValueOps("objectNumJsonRedisTemplateTest");
        objectNumJsonRedisTemplateTest.set(Double.valueOf("9.9999"));
        Object value = objectNumJsonRedisTemplateTest.get();
        objectNumJsonRedisTemplateTest.increment(0.0001);
        String valueStr = stringTemplate.opsForValue().get("objectNumJsonRedisTemplateTest");
        assert value instanceof Double && ((Double) value).doubleValue() == 9.9999;

        log(value, valueStr);
    }

    @Test
    public void objectStrTypRedisTemplateTest() {
        RedisTemplate<String, Object> genericTemplate = enhancedRedisTemplateFactory.getGenericTemplate(EnhancedRedisTemplateFactory.SerializerType.STRING);
        BoundValueOperations<String, Object> objectStrTypRedisTemplateTest = genericTemplate.boundValueOps("objectStrTypRedisTemplateTest");
        objectStrTypRedisTemplateTest.set("objectStrTypRedisTemplateTest");
        Object value = objectStrTypRedisTemplateTest.get();
        String valueStr = stringTemplate.opsForValue().get("objectStrTypRedisTemplateTest");
        assert value.equals("objectStrTypRedisTemplateTest");

        log(value, valueStr);
    }

    @Test
    public void objectNumTypRedisTemplateTest() {
        RedisTemplate<String, Object> genericTemplate = enhancedRedisTemplateFactory.getGenericTemplate(EnhancedRedisTemplateFactory.SerializerType.STRING);
        BoundValueOperations<String, Object> objectNumTypRedisTemplateTest = genericTemplate.boundValueOps("objectNumTypRedisTemplateTest");
        objectNumTypRedisTemplateTest.set(999999);
        Object value = objectNumTypRedisTemplateTest.get();
        objectNumTypRedisTemplateTest.increment();
        String valueStr = stringTemplate.opsForValue().get("objectNumTypRedisTemplateTest");
        assert value.equals("999999");

        BoundValueOperations<String, Object> objectDoubleTypRedisTemplateTest = genericTemplate.boundValueOps("objectDoubleTypRedisTemplateTest");
        objectDoubleTypRedisTemplateTest.set(Double.valueOf("9.9999"));
        Object value2 = objectDoubleTypRedisTemplateTest.get();
        objectDoubleTypRedisTemplateTest.increment(0.0001);
        String valueStr2 = stringTemplate.opsForValue().get("objectDoubleTypRedisTemplateTest");
        assert value2.equals("9.9999");

        log(value, valueStr, value2, valueStr2);
    }
}
