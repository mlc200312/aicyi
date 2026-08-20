package io.github.aicyi.commons.util.bean.orika;

import io.github.aicyi.commons.util.date.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Mr.Min
 * @description LocalDate 类型自定义映射转换器
 * @date 11:35
 **/
public class LocalDateMapperConverter extends BidirectionalConverter<LocalDate, String> {

    private final DateTimeFormatter formatter;

    public LocalDateMapperConverter() {
        this(DateTimeUtils.DATE_PATTERN);
    }

    public LocalDateMapperConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public String convertTo(LocalDate date, Type<String> type, MappingContext mappingContext) {
        if (date == null) {
            return null;
        }
        return date.format(formatter);
    }

    @Override
    public LocalDate convertFrom(String dateStr, Type<LocalDate> type, MappingContext mappingContext) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr.trim(), formatter);
    }
}
