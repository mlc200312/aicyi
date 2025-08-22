package com.aichuangyi.commons.util.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Min
 * @description 映射工具类
 * @date 2025/8/8
 **/
public enum MapperUtils {

    INSTANCE;

    /**
     * 默认字段实例
     */
    private static final MapperFacade MAPPER_FACADE = INSTANCE.createMapperFactory().getMapperFacade();

    /**
     * 默认字段实例集合
     */
    private static Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();

    /**
     * 映射实体（默认字段）
     *
     * @param destType 映射类对象
     * @param src      数据（对象）
     * @return 映射类对象
     */
    public <S, D> D map(S src, Class<D> destType) {
        return MAPPER_FACADE.map(src, destType);
    }

    /**
     * 映射集合（默认字段）
     *
     * @param destType 映射类对象
     * @param src      数据（集合）
     * @return 映射类对象
     */
    public <S, D> List<D> mapAsList(Collection<S> src, Class<D> destType) {
        return CollectionUtils.isEmpty(src) ? Collections.emptyList() : MAPPER_FACADE.mapAsList(src, destType);
    }

    /**
     * 拷贝对象
     *
     * @param src
     * @param dest
     * @param <S>
     * @param <D>
     * @return
     */
    public <S, D> D map(S src, D dest) {
        MAPPER_FACADE.map(src, dest);
        return dest;
    }

    /**
     * 映射实体（自定义配置）
     *
     * @param destType  映射类对象
     * @param src       数据（对象）
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public <D, T> D map(T src, Class<D> destType, Map<String, String> configMap) {
        MapperFacade mapperFacade = this.getMapperFacade(src.getClass(), destType, configMap);
        return mapperFacade.map(src, destType);
    }

    /**
     * 获取自定义映射
     *
     * @param destType  映射类
     * @param srcType   数据映射类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private <S, D> MapperFacade getMapperFacade(Class<S> srcType, Class<D> destType, Map<String, String> configMap) {
        String mapKey = srcType.getCanonicalName() + "_" + destType.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            MapperFactory factory = createMapperFactory();
            ClassMapBuilder classMapBuilder = factory.classMap(srcType, destType);
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = factory.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }

    /**
     * 构建默认 mapperFactory
     *
     * @return
     */
    private MapperFactory createMapperFactory() {
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
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));
        return mapperFactory;
    }
}