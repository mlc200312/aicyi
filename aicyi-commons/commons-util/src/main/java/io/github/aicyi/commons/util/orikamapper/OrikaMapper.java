package io.github.aicyi.commons.util.orikamapper;

import io.github.aicyi.commons.core.BeanMapper;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.util.JsonUtils;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Mr.Min
 * @description 默认的Mapper
 * @date 2019-05-22
 */
public final class OrikaMapper implements BeanMapper {

    private final static JsonCodec DEFAULT_JSON_CODEC = JsonUtils.getInstance();

    private final MapperFactory mapperFactory;

    public OrikaMapper(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
    }

    public OrikaMapper(JsonCodec jsonCodec) {
        this.mapperFactory = getDefaultMapperFactory(jsonCodec);
    }

    public OrikaMapper() {
        this.mapperFactory = getDefaultMapperFactory(DEFAULT_JSON_CODEC);
    }

    public MapperFactory getMapperFactory() {
        return mapperFactory;
    }

    public MapperFacade getMapperFacade() {
        return mapperFactory.getMapperFacade();
    }

    @Override
    public <S, D> D map(S src, Class<D> destType) {
        return src == null ? null : getMapperFacade().map(src, destType);
    }

    @Override
    public <S, D> void map(S src, D dest) {
        if (src == null || dest == null) {
            return;
        }
        getMapperFacade().map(src, dest);
    }

    @Override
    public <S, D> List<D> mapList(Collection<S> src, Class<D> destType) {
        return CollectionUtils.isEmpty(src) ? Collections.emptyList() : getMapperFacade().mapAsList(src, destType);
    }

    private MapperFactory getDefaultMapperFactory(JsonCodec jsonCodec) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
                .useAutoMapping(true)
                .mapNulls(true)
                .build();

        mapperFactory.getConverterFactory().registerConverter(new EnumTypeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new StringEnumTypeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new String2LongMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new TimestampMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new Timestamp2LocalDateTimeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new DateMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new Date2LocalDateTimeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalDateMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalDateTimeMapperConverter());
        mapperFactory.getConverterFactory().registerConverter(new JsonMapperConverter(jsonCodec));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDate.class));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class));

        return mapperFactory;
    }
}
