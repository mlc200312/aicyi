package io.github.aicyi.commons.util.bean.orika;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * @author Mr.Min
 * @description 时间类型自定义映射转换器
 * @date 11:35
 **/
public class String2LongMapperConverter extends BidirectionalConverter<String, Long> {

    @Override
    public Long convertTo(String source, Type<Long> type, MappingContext mappingContext) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        return Long.parseLong(source.trim());
    }

    @Override
    public String convertFrom(Long source, Type<String> type, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        return String.valueOf(source);
    }
}
