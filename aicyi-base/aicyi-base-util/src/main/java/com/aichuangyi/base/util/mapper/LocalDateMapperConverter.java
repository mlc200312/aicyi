package com.aichuangyi.base.util.mapper;

import com.aichuangyi.base.util.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 11:35
 **/
public class LocalDateMapperConverter extends BidirectionalConverter<LocalDate, String> {
    private final String pattern;

    public LocalDateMapperConverter() {
        pattern = DateTimeUtils.DATE_PATTERN;
    }

    public LocalDateMapperConverter(String pattern, MappingContext mappingContext) {
        this.pattern = pattern;
    }


    @Override
    public String convertTo(LocalDate date, Type<String> type, MappingContext mappingContext) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public LocalDate convertFrom(String dateStr, Type<LocalDate> date, MappingContext mappingContext) {
        return DateTimeFormatter.ofPattern(pattern).parse(dateStr, LocalDate::from);
    }
}
