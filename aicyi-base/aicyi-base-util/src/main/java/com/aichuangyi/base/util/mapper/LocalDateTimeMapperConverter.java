package com.aichuangyi.base.util.mapper;

import com.aichuangyi.base.util.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDateTime;

/**
 * @author Mr.Min
 * @description LocalDateTime 类型自定义映射转换器
 * @date 11:35
 **/
public class LocalDateTimeMapperConverter extends BidirectionalConverter<LocalDateTime, String> {
    private final String pattern;

    public LocalDateTimeMapperConverter() {
        pattern = DateTimeUtils.DATE_TIME_PATTERN;
    }

    public LocalDateTimeMapperConverter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String convertTo(LocalDateTime dateTime, Type<String> type, MappingContext mappingContext) {
        return DateTimeUtils.formatLDateTime(dateTime, pattern);
    }

    @Override
    public LocalDateTime convertFrom(String dateStr, Type<LocalDateTime> date, MappingContext mappingContext) {
        return DateTimeUtils.toLDateTime(dateStr, pattern);
    }

}
