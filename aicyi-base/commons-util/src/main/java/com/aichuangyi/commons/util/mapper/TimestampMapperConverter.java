package com.aichuangyi.commons.util.mapper;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.sql.Timestamp;

/**
 * @author Mr.Min
 * @description 时间戳类型自定义映射转换器
 * @date 11:35
 **/
public class TimestampMapperConverter extends BidirectionalConverter<Timestamp, String> {

    @Override
    public String convertTo(Timestamp timestamp, Type<String> type, MappingContext mappingContext) {
        return timestamp.getTime() + "";
    }

    @Override
    public Timestamp convertFrom(String dateStr, Type<Timestamp> date, MappingContext mappingContext) {
        return new Timestamp(Long.parseLong(dateStr));
    }
}
