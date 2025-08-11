package com.aichuangyi.demo.redis.client;

import com.aichuangyi.commons.util.json.JacksonConverter;
import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.domain.Example;
import com.aichuangyi.test.domain.Message;
import com.aichuangyi.test.util.DataSource;
import com.aicyiframework.redis.EnhancedRedisTemplateFactory;
import com.fasterxml.jackson.databind.JavaType;
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
@SpringBootTest(classes = AicyiDemoApplication.class)
public class EnhancedRedisTemplateFactoryTest extends BaseLoggerTest {
    @Autowired
    private EnhancedRedisTemplateFactory enhancedRedisTemplateFactory;

    private RedisTemplate<String, String> stringTemplate;
    private RedisTemplate<String, Example> exampleRedisTemplate;
    private RedisTemplate<String, Example> objectJdkRedisTemplate;
    private RedisTemplate<String, Message> objectXmlRedisTemplate;
    private RedisTemplate<String, Example> objectJsonRedisTemplate;
    private RedisTemplate<String, String> objectStringRedisTemplate;

    @Before
    public void before() {
        stringTemplate = enhancedRedisTemplateFactory.getStringTemplate();

        JavaType javaType = new JacksonConverter().constructType(Example.class);
        exampleRedisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(javaType);

        objectJdkRedisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.JDK, Example.class);
        objectXmlRedisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.XML, Message.class);
        objectJsonRedisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON, Example.class);
        objectStringRedisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.STRING, String.class);
    }

    @Test
    public void stringRedisTemplateTest() {
        BoundValueOperations<String, String> stringRedisTemplate = stringTemplate.boundValueOps("stringRedisTemplateTest");
        stringRedisTemplate.set("stringRedisTemplateTest");
        String stringRedisTemplateTestValue = stringRedisTemplate.get();

        BoundValueOperations<String, String> intRedisTemplate = stringTemplate.boundValueOps("intRedisTemplateTest");
        intRedisTemplate.set("999999");
        intRedisTemplate.increment();
        String intRedisTemplateValue = intRedisTemplate.get();

        log("stringRedisTemplateTest", stringRedisTemplateTestValue, intRedisTemplateValue);
    }

    @Test
    public void exampleRedisTemplateTest() {
        JacksonConverter jacksonConverter = new JacksonConverter();
        BoundValueOperations<String, Example> exampleRedisTemplateTest = exampleRedisTemplate.boundValueOps("exampleRedisTemplateTest");
        exampleRedisTemplateTest.set(DataSource.getExample());
        Example example = exampleRedisTemplateTest.get();

        JavaType stringType = jacksonConverter.constructType(String.class);
        RedisTemplate<String, String> stringExampleRedisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(stringType);
        BoundValueOperations<String, String> stringExampleRedisTemplateTest = stringExampleRedisTemplate.boundValueOps("stringExampleRedisTemplateTest");
        stringExampleRedisTemplateTest.set("stringExampleRedisTemplateTest");
        String stringExampleRedisTemplateTestValue = stringExampleRedisTemplateTest.get();

        JavaType intType = jacksonConverter.constructType(Integer.class);
        RedisTemplate<String, Integer> intExampleRedisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(intType);
        BoundValueOperations<String, Integer> intExampleRedisTemplateTest = intExampleRedisTemplate.boundValueOps("intExampleRedisTemplateTest");
        intExampleRedisTemplateTest.set(999999);
        Integer intExampleRedisTemplateTestValue = intExampleRedisTemplateTest.get();

        JavaType exampleType = jacksonConverter.constructParametricType(List.class, jacksonConverter.constructType(Example.class));
        RedisTemplate<String, List<Example>> listExampleRedisTemplate = enhancedRedisTemplateFactory.getJsonTemplate(exampleType);
        BoundValueOperations<String, List<Example>> listExampleRedisTemplateTest = listExampleRedisTemplate.boundValueOps("listExampleRedisTemplateTest");
        listExampleRedisTemplateTest.set(DataSource.getExampleList());
        Object listExampleRedisTemplateTestValue = listExampleRedisTemplateTest.get();

        log("exampleRedisTemplateTest", example, stringExampleRedisTemplateTestValue, intExampleRedisTemplateTestValue, listExampleRedisTemplateTestValue);
    }

    @Test
    public void objectJdkRedisTemplateTest() {
        BoundValueOperations<String, Example> objectJdkRedisTemplateTest = objectJdkRedisTemplate.boundValueOps("objectJdkRedisTemplateTest");
        objectJdkRedisTemplateTest.set(DataSource.getExample());
        Object objectJdkRedisTemplateTestValue = objectJdkRedisTemplateTest.get();

        log("objectJdkRedisTemplateTest", objectJdkRedisTemplateTestValue);
    }


    @Test
    public void objectXmlRedisTemplateTest() {
//        RedisTemplate<String, Object> objectXmlRedisTemplate = new RedisTemplate<>();
//        objectXmlRedisTemplate.setConnectionFactory(enhancedRedisTemplateFactory.getRedisConnectionFactory());
//        objectXmlRedisTemplate.setKeySerializer(new StringRedisSerializer());
//
//        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        marshaller.setPackagesToScan("com.aichuangyi.test.domain");
//
//        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
//        unmarshaller.setPackagesToScan("com.aichuangyi.test.domain");
//
//        OxmSerializer oxmSerializer = new OxmSerializer(marshaller, unmarshaller);
//        objectXmlRedisTemplate.setValueSerializer(oxmSerializer);
//
//        objectXmlRedisTemplate.afterPropertiesSet();

        BoundValueOperations<String, Message> objectXmlRedisTemplateTest = objectXmlRedisTemplate.boundValueOps("objectXmlRedisTemplateTest");
        objectXmlRedisTemplateTest.set(DataSource.getMessage());
        Message objectXmlRedisTemplateTestValue = objectXmlRedisTemplateTest.get();

        log("objectXmlRedisTemplateTest", objectXmlRedisTemplateTestValue);
    }

    @Test
    public void objectJsonRedisTemplateTest() {
        BoundValueOperations<String, Example> objectJsonRedisTemplateTest = objectJsonRedisTemplate.boundValueOps("objectJsonRedisTemplateTest");
        objectJsonRedisTemplateTest.set(DataSource.getExample());
        Example objectJsonRedisTemplateTestValue = objectJsonRedisTemplateTest.get();

        RedisTemplate<String, String> stringJsonRedisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON, String.class);
        BoundValueOperations<String, String> stringJsonRedisTemplateTest = stringJsonRedisTemplate.boundValueOps("stringJsonRedisTemplateTest");
        stringJsonRedisTemplateTest.set("stringJsonRedisTemplateTest");
        String stringJsonRedisTemplateTestValue = stringJsonRedisTemplateTest.get();

        RedisTemplate<String, Double> doubleJsonRedisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.JSON, Double.class);
        BoundValueOperations<String, Double> doubleJsonRedisTemplateTest = doubleJsonRedisTemplate.boundValueOps("doubleJsonRedisTemplateTest");
        doubleJsonRedisTemplateTest.set(Double.valueOf("9.9999"));
        Double doubleJsonRedisTemplateTestValue = doubleJsonRedisTemplateTest.get();

        log("objectJsonRedisTemplateTest", objectJsonRedisTemplateTestValue, stringJsonRedisTemplateTestValue, doubleJsonRedisTemplateTestValue);
    }

    @Test
    public void objectPrimitiveTypRedisTemplateTest() {
        BoundValueOperations<String, String> stringPrimitiveTypRedisTemplateTest = objectStringRedisTemplate.boundValueOps("stringPrimitiveTypRedisTemplateTest");
        stringPrimitiveTypRedisTemplateTest.set("stringPrimitiveTypRedisTemplateTest");
        String stringPrimitiveTypRedisTemplateTestValue = stringPrimitiveTypRedisTemplateTest.get();

        RedisTemplate<String, Integer> intPrimitiveTypRedisTemplateTest = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.STRING, Integer.class);
        BoundValueOperations<String, Integer> intPrimitiveTypRedisTemplate = intPrimitiveTypRedisTemplateTest.boundValueOps("intPrimitiveTypRedisTemplateTest");
        intPrimitiveTypRedisTemplate.set(999999);
        intPrimitiveTypRedisTemplate.increment();
        Integer intPrimitiveTypRedisTemplateValue = intPrimitiveTypRedisTemplate.get();

        RedisTemplate<String, Double> doublePrimitiveTypRedisTemplate = enhancedRedisTemplateFactory.getTemplate(EnhancedRedisTemplateFactory.SerializerType.STRING, Double.class);
        BoundValueOperations<String, Double> doublePrimitiveTypRedisTemplateTest = doublePrimitiveTypRedisTemplate.boundValueOps("doublePrimitiveTypRedisTemplateTest");
        doublePrimitiveTypRedisTemplateTest.set(Double.valueOf("9.9999"));
        Double doublePrimitiveTypRedisTemplateTestValue = doublePrimitiveTypRedisTemplateTest.get();

        log("objectPrimitiveTypRedisTemplateTest", stringPrimitiveTypRedisTemplateTestValue, intPrimitiveTypRedisTemplateValue, doublePrimitiveTypRedisTemplateTestValue);
    }
}
