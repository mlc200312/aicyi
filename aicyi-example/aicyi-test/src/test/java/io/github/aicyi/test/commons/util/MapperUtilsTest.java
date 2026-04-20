package io.github.aicyi.test.commons.util;

import io.github.aicyi.example.domain.Example;
import io.github.aicyi.example.domain.StudentBean;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.dto.ExampleResp;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.commons.util.mapper.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 10:54
 **/
public class MapperUtilsTest extends BaseLoggerTest {
    private static final MapperFacade MAPPER_FACADE;

    static {
        FieldMapBuilder.FieldBuildConfig config = FieldMapBuilder.create()
                .add(Example.Fields.id, ExampleResp.Fields.uuid)
                .add("student.score", "student.score0")
                .add("student.username", "student.userName")
                .ignore(Example.Fields.nothing)
                .build();

        MAPPER_FACADE = MapperUtils.INSTANCE.getMapperFacade(Example.class, ExampleResp.class, config.getFieldMap(), config.getIgnoreFields());
    }

    @Test
    @Override
    public void test() {
        Example example = DataSource.getExample();
        ExampleResp exampleResp = MAPPER_FACADE.map(example, ExampleResp.class);
        assert exampleResp != null;

        ExampleResp exampleResp2 = DataSource.getExampleResp();
        Example example2 = MAPPER_FACADE.map(exampleResp2, Example.class);
        assert example2 != null;

        log("test", exampleResp, example2);
    }

    @Test
    public void mapTest2() {
        ExampleResp exampleResp = MapperUtils.INSTANCE.map(DataSource.getExample(), ExampleResp.class);
        assert exampleResp != null;

        Example example = MapperUtils.INSTANCE.map(DataSource.getExampleResp(), Example.class);
        assert example != null;

        List<ExampleResp> exampleRespList = MapperUtils.INSTANCE.mapAsList(DataSource.getExampleList(), ExampleResp.class);
        assert CollectionUtils.isNotEmpty(exampleRespList);

        User user = MapperUtils.INSTANCE.map(new StudentBean(), User.class);
        assert user != null;

        log("mapTest2", exampleResp, example, exampleRespList, user);
    }

    @Test
    public void mapTest3() {
        FieldMapBuilder.FieldBuildConfig config = FieldMapBuilder.create()
                .add(Example.Fields.id, ExampleResp.Fields.uuid)
                .add("student.score", "student.score0")
                .add("student.username", "student.userName")
                .ignore("nothing")
                .build();
        ExampleResp exampleResp = MapperUtils.INSTANCE.map(DataSource.getExample(), ExampleResp.class, config);
        assert exampleResp != null && exampleResp.getStudent() != null && exampleResp.getStudent().getScore0() != null && exampleResp.getStudent().getUserName() != null;

        FieldMapBuilder.FieldBuildConfig config2 = FieldMapBuilder.create()
                .add(ExampleResp.Fields.uuid, Example.Fields.id)
                .add("student.score0", "student.score")
                .add("student.userName", "student.username")
                .ignore("nothing")
                .build();
        Example example = MapperUtils.INSTANCE.map(DataSource.getExampleResp(), Example.class, config2);
        assert example != null && example.getStudent() != null && example.getStudent().getScore() != null && example.getStudent().getUsername() != null;

        log("mapTest3", exampleResp, example);
    }

    @Test
    public void mapTest4() {
        Example example = MapperUtils.INSTANCE.map(DataSource.getExample(), new Example());
        assert example != null;

        ExampleResp exampleResp = MapperUtils.INSTANCE.map(DataSource.getExample(), new ExampleResp());
        assert exampleResp != null;

        log("mapTest4", example, exampleResp);
    }
}
