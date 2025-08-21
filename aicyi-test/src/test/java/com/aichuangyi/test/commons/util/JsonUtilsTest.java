package com.aichuangyi.test.commons.util;

import com.aichuangyi.commons.core.JsonConverter;
import com.aichuangyi.commons.lang.BaseBean;
import com.aichuangyi.commons.lang.UserInfo;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.commons.util.json.JacksonConverter;
import com.aichuangyi.commons.util.json.JacksonHelper;
import com.aichuangyi.commons.util.json.JsonUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.domain.Example;
import com.aichuangyi.test.util.DataSource;
import com.fasterxml.jackson.databind.DeserializationFeature;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 21:36
 **/
public class JsonUtilsTest extends BaseLoggerTest {
    private A a;
    private List<A> aList;
    private Map<String, A<B<C>>> aMap;

    @Before
    public void beforeTest() {
        a = new A(new B(new C()));
        aList = Lists.newArrayList(a);
        aMap = new HashMap<>();
        aMap.put(IdGenerator.generateV7Id(), a);
    }

    @Test
    public void test() {
        JsonConverter instance = JsonUtils.getInstance();
        List<Example> exampleList = DataSource.getExampleList();

        String json = instance.toJson(a);
        String json1 = instance.toJson(aList);
        String json2 = instance.toJson(aMap);

        String json3 = instance.toJson(exampleList);

        log("test", json, json1, json2, json3);
    }

    @Test
    public void parseJson() {
        JsonConverter instance = JsonUtils.getInstance();
        String json = DataSource.getExampleJson();

        Object parse = instance.parse(json);
        Type type = instance.constructType(Example.class);
        Example parse1 = instance.parse(json, type);

        log("parseJson", parse, parse1);
    }

    @Test
    public void parseJson2() {
        JacksonConverter jacksonConverter = new JacksonConverter();
        jacksonConverter.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String userJson = "{\"id\":613294732759531520,\"age\":0,\"idCard\":\"1f07da341eb26024b3f927826ef0e6e2\",\"mobile\":\"13010496590\",\"genderType\":\"WOMAN\",\"birthday\":\"2025-08-20\",\"userId\":\"610780341698822144\",\"username\":\"邓纨仪\",\"deviceId\":\"1f07da341ee56475b3f927826ef0e6e2\",\"isMasterDevice\":false}";
        UserInfo parseUser = jacksonConverter.parse(userJson, JacksonHelper.getType(UserInfo.class));

        log("parseJson2", parseUser);
    }

    @Test
    public void parseList() {
        JsonConverter instance = JsonUtils.getInstance();
        String json = DataSource.getExampleListJson();

        List<Example> exampleList = instance.parseList(json);
        Type type = instance.constructType(Example.class);
        List<Example> exampleList2 = instance.parseList(json, type);

        log("parseList", exampleList, exampleList2);
    }

    @Test
    public void parseMap() {
        JsonConverter instance = JsonUtils.getInstance();
        String json = DataSource.getExampleMapJson();

        Map<Object, Example> exampleMap = instance.parseMap(json);
        Map<String, Example> exampleMap1 = instance.parseMap(json, Example.class);
        Type ktype = instance.constructType(Long.class);
        Type vtype = instance.constructType(Example.class);
        Map<Long, Example> exampleMap2 = instance.parseMap(json, ktype, vtype);

        log("parseMap", exampleMap, exampleMap1, exampleMap2);
    }

    @Test
    public void isEmptyJsonTest() {
        JsonConverter instance = JsonUtils.getInstance();

        boolean emptyJSON = instance.isEmptyJSON("");
        boolean emptyJSON1 = instance.isEmptyJSON("{}");
        boolean emptyJSON2 = instance.isEmptyJSON("[]");
        boolean emptyJSON3 = instance.isEmptyJSON("123");

        log("isEmptyJsonTest", emptyJSON, emptyJSON1, emptyJSON2, emptyJSON3);
    }

    @Getter
    @Setter
    public static class A<T> extends BaseBean {
        private String a = "a";
        private T b;

        public A() {
        }

        public A(T b) {
            this.b = b;
        }
    }

    @Getter
    @Setter
    public static class B<T> extends BaseBean {
        private String b = "b";
        private T c;

        public B() {
        }

        public B(T c) {
            this.c = c;
        }
    }

    @Getter
    @Setter
    public static class C extends BaseBean {
        private String c = "c";
    }
}
