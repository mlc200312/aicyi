package io.github.aicyi.commons.util.orikamapper;

import io.github.aicyi.commons.util.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 时间类型自定义映射转换器
 * @date 11:35
 **/
public class Date2LocalDateTimeMapperConverter extends BidirectionalConverter<Date, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(Date date, Type<LocalDateTime> type, MappingContext mappingContext) {
        if (Objects.isNull(date)) {
            return null;
        }
        return DateTimeUtils.toLDateTime(date);
    }

    @Override
    public Date convertFrom(LocalDateTime dateTime, Type<Date> type, MappingContext mappingContext) {
        if (Objects.isNull(dateTime)) {
            return null;
        }
        return DateTimeUtils.toDate(dateTime);
    }

}
