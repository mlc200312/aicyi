package io.github.aicyi.test.commons.util;

import io.github.aicyi.example.domain.Example;
import io.github.aicyi.example.dto.ExampleResp;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.commons.util.mapper.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
                //useAutoMapping 控制是否启用 Orika 的自动字段映射机制，当设置为 true 时（默认值），Orika 会自动尝试匹配源对象和目标对象的同名属性。
                .useAutoMapping(true)
                //mapNulls 控制是否将源对象的 null 值映射到目标对象，当设置为 true 时，源字段为 null 会覆盖目标字段的现有值。
                .mapNulls(true)
                .build();
        // 注册自定义转换器
        mapperFactory.getConverterFactory().registerConverter(new EnumTypeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new StringEnumTypeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new TimestampMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new DateMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalDateMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalDateTimeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new JsonMapperConverter());

        mapperFactory.classMap(Example.class, ExampleResp.class)
                .field(Example.Fields.id, ExampleResp.Fields.uuid)
                .field("student.score", "student.score0")
                .field("student.username", "student.userName")
                .exclude(Example.Fields.nothing)
                .byDefault()
                .register();
        MAPPER_FACADE = mapperFactory.getMapperFacade();
    }

    @Test
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
        MapperUtils instance = MapperUtils.INSTANCE;

        ExampleResp exampleResp = instance.map(DataSource.getExample(), ExampleResp.class);

        assert exampleResp != null;

        Example example = instance.map(DataSource.getExampleResp(), Example.class);

        assert example != null;

        List<ExampleResp> exampleRespList = instance.mapAsList(DataSource.getExampleList(), ExampleResp.class);

        assert exampleRespList != null && exampleRespList.size() > 0;

        log("mapTest2", exampleResp, example, exampleRespList);
    }

    @Test
    public void mapTest3() {
        MapperUtils instance = MapperUtils.INSTANCE;

        Map<String, String> configMap = new HashMap<>();
        configMap.put(Example.Fields.id, ExampleResp.Fields.uuid);
        configMap.put("student.score", "student.score0");
        configMap.put("student.username", "student.userName");
        ExampleResp exampleResp = instance.map(DataSource.getExample(), ExampleResp.class, configMap);

        assert exampleResp != null;

        Map<String, String> configMap2 = new HashMap<>();
        configMap2.put(ExampleResp.Fields.uuid, Example.Fields.id);
        configMap2.put("student.score0", "student.score");
        configMap2.put("student.userName", "student.username");
        Example example = instance.map(DataSource.getExampleResp(), Example.class, configMap2);

        assert example != null;

        log("mapTest3", exampleResp, example);
    }

    @Test
    public void mapTest4() {
        MapperUtils instance = MapperUtils.INSTANCE;

        Example example = instance.map(DataSource.getExample(), new Example());

        assert example != null;

        ExampleResp exampleResp = instance.map(DataSource.getExample(), new ExampleResp());

        assert exampleResp != null;

        log("mapTest4", example, exampleResp);
    }
}
