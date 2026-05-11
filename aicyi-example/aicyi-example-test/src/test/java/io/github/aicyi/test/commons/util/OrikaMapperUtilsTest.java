package io.github.aicyi.test.commons.util;

import io.github.aicyi.test.domain.Example;
import io.github.aicyi.test.dto.ExampleResp;
import io.github.aicyi.test.domain.ExampleBean;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.commons.util.orikamapper.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 10:54
 **/
public class OrikaMapperUtilsTest extends BaseLoggerTest {
    private OrikaMapperRegistry.MappingConfig mappingConfig;

    @Before
    public void beforeTest() {
        mappingConfig = OrikaMapperRegistry.config()
                .map(Example.Fields.id, ExampleResp.Fields.uuid)
                .map("student.score", "student.score0")
                .ignore("nothing");
    }

    @Test
    @Override
    public void test() {
        ExampleBean exampleBean = DataSource.getExample();
        Example example = OrikaMapperRegistry.INSTANCE.map(exampleBean, Example.class);
        assert example != null;

        ExampleResp exampleResp = OrikaMapperRegistry.INSTANCE.map(exampleBean, ExampleResp.class, mappingConfig);
        assert exampleResp != null && exampleResp.getStudent() != null && exampleResp.getStudent().getScore0() != null;
        Double score0 = Double.valueOf(exampleResp.getStudent().getScore0());
        assert score0.equals(example.getStudent().getScore());

        log(example, exampleResp);
    }

    @Test
    public void test2() {
        ExampleResp exampleResp = DataSource.getExampleResp();
        ExampleBean exampleBean = OrikaMapperRegistry.INSTANCE.map(exampleResp, ExampleBean.class, mappingConfig.reversed());
        assert exampleBean != null;

        Example example = OrikaMapperRegistry.INSTANCE.map(exampleResp, Example.class, mappingConfig.reversed());
        assert example != null && example.getStudent() != null;
        Double score0 = Double.valueOf(exampleResp.getStudent().getScore0());
        assert score0.equals(example.getStudent().getScore());

        log(exampleBean, example);
    }

    @Test
    public void test3() {
        List<ExampleBean> exampleBeanList = DataSource.getExampleList();
        List<Example> exampleList = OrikaMapperRegistry.INSTANCE.mapList(exampleBeanList, Example.class);
        assert exampleList != null;

        List<ExampleResp> exampleRespList = OrikaMapperRegistry.INSTANCE.mapAsList(exampleBeanList, ExampleResp.class, mappingConfig);
        assert exampleRespList != null;

        log(exampleList, exampleRespList);
    }
}
