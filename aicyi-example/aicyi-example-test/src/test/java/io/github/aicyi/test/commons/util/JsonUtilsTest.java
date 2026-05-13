package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.security.token.JWTInfo;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;
import io.github.aicyi.commons.util.JsonUtils;
import io.github.aicyi.test.domain.Example;
import io.github.aicyi.test.domain.ExampleBean;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import lombok.Getter;
import lombok.Setter;
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
        aList = Arrays.asList(a);
        aMap = new HashMap<>();
        aMap.put(IdUtils.generateV7Id(), a);
    }

    @Test
    public void test() {
        JsonCodec instance = JsonUtils.getInstance();
        List<ExampleBean> exampleList = DataSource.getExampleList();
        String json = instance.toJson(a);
        String json1 = instance.toJson(aList);
        String json2 = instance.toJson(aMap);
        String json3 = instance.toJson(exampleList);

        log(json, json1, json2, json3);
    }

    @Test
    public void test2() {
        JsonCodec instance = JsonUtils.getInstance();
        String json = DataSource.getExampleJson();
        Object parse = instance.fromJson(json, Object.class);
        assert parse instanceof Map;

        Type type = instance.createType(Example.class);
        Example parse1 = instance.fromJson(json, type);
        assert parse1 != null;

        log(parse, parse1);
    }

    @Test
    public void test3() {
        JacksonJsonCodec instance = JacksonJsonCodec.DEFAULT;
        String json = "{\"id\":613294732759531520,\"age\":0,\"idCard\":\"1f07da341eb26024b3f927826ef0e6e2\",\"mobile\":\"13010496590\",\"genderType\":\"WOMAN\",\"birthday\":\"2025-08-20\",\"userId\":\"610780341698822144\",\"username\":\"邓纨仪\",\"deviceId\":\"1f07da341ee56475b3f927826ef0e6e2\",\"isMasterDevice\":false}";
        JWTInfo parse = instance.fromJson(json, JacksonTypeFactory.typeOf(JWTInfo.class));
        assert parse != null;

        log(parse);
    }

    @Test
    public void test4() {
        JsonCodec instance = JsonUtils.getInstance();
        String json = DataSource.getExampleListJson();
        List<Example> exampleList = instance.fromJsonList(json, Example.class);
        assert exampleList != null;

        Type type = instance.createType(Example.class);
        List<Example> exampleList1 = instance.fromJsonList(json, type);
        assert exampleList1 != null;

        log(exampleList, exampleList1);
    }

    @Test
    public void test5() {
        JsonCodec instance = JsonUtils.getInstance();
        String json = DataSource.getExampleMapJson();
        Map<Object, Object> exampleMap = instance.fromJsonMap(json, Object.class, Object.class);
        assert exampleMap != null;

        Map<String, Example> exampleMap1 = instance.fromJsonMap(json, String.class, Example.class);
        assert exampleMap1 != null;

        Type ktype = instance.createType(Long.class);
        Type vtype = instance.createType(Example.class);
        Map<Long, Example> exampleMap2 = instance.fromJsonMap(json, ktype, vtype);
        assert exampleMap2 != null;

        log(exampleMap, exampleMap1, exampleMap2);
    }

    @Test
    public void test6() {
        JsonCodec instance = JsonUtils.getInstance();
        boolean emptyJSON = instance.isEmptyJson("");
        boolean emptyJSON1 = instance.isEmptyJson("{}");
        boolean emptyJSON2 = instance.isEmptyJson("[]");
        boolean emptyJSON3 = instance.isEmptyJson("123");
        assert emptyJSON && emptyJSON1 && emptyJSON2 && !emptyJSON3;

        log(emptyJSON, emptyJSON1, emptyJSON2, emptyJSON3);
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
