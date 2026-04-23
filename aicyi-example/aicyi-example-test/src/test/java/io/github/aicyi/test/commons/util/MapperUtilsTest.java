package io.github.aicyi.test.commons.util;

import io.github.aicyi.test.domain.Example;
import io.github.aicyi.test.dto.ExampleResp;
import io.github.aicyi.test.domain.ExampleBean;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.commons.util.mapper.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 10:54
 **/
public class MapperUtilsTest extends BaseLoggerTest {
    private FieldMapBuilder.FieldMapConfig config;

    @Before
    public void beforeTest() {
        config = FieldMapBuilder.create()
                .add(Example.Fields.id, ExampleResp.Fields.uuid)
                .add("student.score", "student.score0")
                .add("student.username", "student.userName")
                .ignore("nothing")
                .build();
    }

    @Test
    @Override
    public void test() {
        ExampleBean exampleBean = DataSource.getExample();
        Example example = MapperUtils.INSTANCE.map(exampleBean, Example.class);
        assert example != null;

        ExampleResp exampleResp = MapperUtils.INSTANCE.map(exampleBean, ExampleResp.class, config);
        assert exampleResp != null && exampleResp.getStudent() != null && Double.valueOf(exampleResp.getStudent().getScore0()) == example.getScore();

        log("test", example, exampleResp);
    }

    @Test
    public void test2() {
        ExampleResp exampleResp = DataSource.getExampleResp();
        ExampleBean exampleBean = MapperUtils.INSTANCE.map(exampleResp, ExampleBean.class);
        assert exampleBean != null;

        Example example = MapperUtils.INSTANCE.map(exampleResp, Example.class, config.reverse());
        assert example != null && example.getStudent() != null && example.getStudent().getScore() == Double.valueOf(exampleResp.getStudent().getScore0());

        log("test", exampleBean, example);
    }

    @Test
    public void test3() {
        List<ExampleBean> exampleBeanList = DataSource.getExampleList();
        List<Example> exampleList = MapperUtils.INSTANCE.mapAsList(exampleBeanList, Example.class);
        assert exampleList != null;

        List<ExampleResp> exampleRespList = MapperUtils.INSTANCE.mapAsList(exampleBeanList, ExampleResp.class, config);
        assert exampleRespList != null;

        log("test", exampleList, exampleRespList);
    }
}
