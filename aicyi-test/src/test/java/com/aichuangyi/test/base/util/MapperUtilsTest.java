package com.aichuangyi.test.base.util;

import com.aichuangyi.base.util.mapper.*;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.domain.Example;
import com.aichuangyi.test.dto.ExampleResp;
import com.aichuangyi.test.util.DataSource;
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
                .exclude(Example.Fields.nothing)
                .byDefault()
                .register();
        MAPPER_FACADE = mapperFactory.getMapperFacade();
    }

    @Test
    public void mapperTest() {
        Example example = DataSource.getExample();
        ExampleResp exampleResp = MAPPER_FACADE.map(example, ExampleResp.class);
        ExampleResp exampleResp2 = DataSource.getExampleResp();
        Example example2 = MAPPER_FACADE.map(exampleResp2, Example.class);

        log("mapTest", exampleResp, example2);
    }

    @Test
    public void mapperUtilsTest() {
        MapperUtils instance = MapperUtils.INSTANCE;

        ExampleResp exampleResp = instance.map(DataSource.getExample(), ExampleResp.class);
        Example example = instance.map(DataSource.getExampleResp(), Example.class);
        List<ExampleResp> exampleRespList = instance.mapAsList(DataSource.getExampleList(), ExampleResp.class);

        log("mapperUtilsTest", exampleResp, example, exampleRespList);
    }

    @Test
    public void mapperUtilsTest2() {
        MapperUtils instance = MapperUtils.INSTANCE;

        Map<String, String> configMap = new HashMap<>();
        configMap.put(Example.Fields.id, ExampleResp.Fields.uuid);
        configMap.put("student.score", "student.score0");
        ExampleResp exampleResp = instance.map(DataSource.getExample(), ExampleResp.class, configMap);

        Map<String, String> configMap2 = new HashMap<>();
        configMap2.put(ExampleResp.Fields.uuid, Example.Fields.id);
        configMap2.put("student.score0", "student.score");
        Example example = instance.map(DataSource.getExampleResp(), Example.class, configMap2);

        log("mapperUtilsTest2", exampleResp, example);
    }

    @Test
    public void mapperUtilsTest3() {
        MapperUtils instance = MapperUtils.INSTANCE;

        Example example = instance.map(DataSource.getExample(), new Example());
        ExampleResp exampleResp = instance.map(DataSource.getExampleResp(), new ExampleResp());

        log("mapperUtilsTest3", example, exampleResp);
    }
}
