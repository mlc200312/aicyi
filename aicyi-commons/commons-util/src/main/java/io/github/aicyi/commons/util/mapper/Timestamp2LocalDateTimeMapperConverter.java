package io.github.aicyi.commons.util.mapper;

import io.github.aicyi.commons.util.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 时间类型自定义映射转换器
 * @date 11:35
 **/
public class Timestamp2LocalDateTimeMapperConverter extends BidirectionalConverter<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(Timestamp timestamp, Type<LocalDateTime> type, MappingContext mappingContext) {
        if (Objects.isNull(timestamp)) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    @Override
    public Timestamp convertFrom(LocalDateTime dateTime, Type<Timestamp> type, MappingContext mappingContext) {
        if (Objects.isNull(dateTime)) {
            return null;
        }
        return new Timestamp(DateTimeUtils.toLong(dateTime));
    }
}
