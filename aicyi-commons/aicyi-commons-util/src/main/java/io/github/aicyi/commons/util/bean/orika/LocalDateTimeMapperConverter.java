package io.github.aicyi.commons.util.bean.orika;

import io.github.aicyi.commons.util.date.DateTimeUtils;
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
        return DateTimeUtils.format(dateTime, pattern);
    }

    @Override
    public LocalDateTime convertFrom(String dateStr, Type<LocalDateTime> type, MappingContext mappingContext) {
        // 自动解析，兼容 yyyy-MM-dd HH:mm:ss 与 yyyy-MM-dd HH:mm:ss.SSS 等格式
        return DateTimeUtils.parseAuto(dateStr);
    }

}
