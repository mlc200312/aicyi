package com.aichuangyi.commons.util.mapper;

import com.aichuangyi.commons.util.DateUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.Date;

/**
 * @author Mr.Min
 * @description 时间类型自定义映射转换器
 * @date 11:35
 **/
public class DateMapperConverter extends BidirectionalConverter<Date, String> {

    @Override
    public String convertTo(Date date, Type<String> type, MappingContext mappingContext) {
        return DateUtils.formatDate(date);
    }

    @Override
    public Date convertFrom(String dateStr, Type<Date> date, MappingContext mappingContext) {
        return DateUtils.parseDate(dateStr);
    }

}
