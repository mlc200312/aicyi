package io.github.aicyi.commons.util.mapper;

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
import java.util.concurrent.ConcurrentMap;

/**
 * @author Mr.Min
 * @description Mapper工具类
 * @date 2026/4/21
 **/
public enum MapperUtils {

    INSTANCE;

    private static final MapperFacade DEFAULT_MAPPER = INSTANCE.createMapperFactory().getMapperFacade();

    private static final ConcurrentMap<String, MapperFacade> CACHE = new ConcurrentHashMap<>();

    public <S, D> D map(S src, Class<D> destType) {
        return src == null ? null : DEFAULT_MAPPER.map(src, destType);
    }

    public <S, D> D map(S src, D dest) {
        if (src == null || dest == null) {
            return dest;
        }
        DEFAULT_MAPPER.map(src, dest);
        return dest;
    }

    public <S, D> List<D> mapAsList(Collection<S> src, Class<D> destType) {
        return CollectionUtils.isEmpty(src)
                ? Collections.emptyList()
                : DEFAULT_MAPPER.mapAsList(src, destType);
    }

    public <S, D> D map(S src, Class<D> destType, FieldMapBuilder.FieldMapConfig config) {
        if (src == null) {
            return null;
        }
        MapperFacade mapperFacade = getMapperFacade(
                src.getClass(),
                destType,
                config
        );
        return mapperFacade.map(src, destType);
    }

    public <S, D> D map(S src, D dest, FieldMapBuilder.FieldMapConfig config) {
        if (src == null || dest == null) {
            return dest;
        }
        MapperFacade mapperFacade = getMapperFacade(
                src.getClass(),
                dest.getClass(),
                config
        );
        mapperFacade.map(src, dest);
        return dest;
    }

    public <S, D> List<D> mapAsList(Collection<S> src, Class<D> destType, FieldMapBuilder.FieldMapConfig config) {
        if (CollectionUtils.isEmpty(src)) {
            return Collections.emptyList();
        }

        Class<?> srcType = src.iterator().next().getClass();
        MapperFacade mapperFacade = getMapperFacade(srcType, destType, config);
        return mapperFacade.mapAsList(src, destType);
    }

    private <S, D> MapperFacade getMapperFacade(
            Class<S> srcType,
            Class<D> destType,
            FieldMapBuilder.FieldMapConfig config) {

        String cacheKey = buildCacheKey(srcType, destType, config);

        return CACHE.computeIfAbsent(cacheKey,
                key -> buildMapperFacade(srcType, destType, config));
    }

    private <S, D> MapperFacade buildMapperFacade(
            Class<S> srcType,
            Class<D> destType,
            FieldMapBuilder.FieldMapConfig config) {

        MapperFactory factory = createMapperFactory();

        ClassMapBuilder<S, D> classMapBuilder = factory.classMap(srcType, destType);

        Optional.ofNullable(config.getFieldMap())
                .orElse(Collections.emptyMap())
                .forEach(classMapBuilder::field);

        Optional.ofNullable(config.getIgnoreFields())
                .orElse(Collections.emptyList())
                .forEach(classMapBuilder::exclude);

        classMapBuilder.byDefault().register();

        return factory.getMapperFacade();
    }

    private String buildCacheKey(
            Class<?> srcType,
            Class<?> destType,
            FieldMapBuilder.FieldMapConfig config) {
        return srcType.getName() + "->" + destType.getName() + "|" + Objects.hash(config.getFieldMap(), config.getIgnoreFields());
    }

    private MapperFactory createMapperFactory() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
                .useAutoMapping(true)
                .mapNulls(true)
                .build();

        mapperFactory.getConverterFactory().registerConverter(new EnumTypeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new StringEnumTypeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new TimestampMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new Timestamp2LocalDateTimeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new DateMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new Date2LocalDateTimeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalDateMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalDateTimeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new JsonMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));

        return mapperFactory;
    }
}