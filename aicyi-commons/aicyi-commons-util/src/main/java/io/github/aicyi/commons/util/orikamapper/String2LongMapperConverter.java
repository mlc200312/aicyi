package io.github.aicyi.commons.util.orikamapper;

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
    public Long convertTo(String var1, Type<Long> type, MappingContext mappingContext) {
        if (var1 == null || var1.trim().isEmpty()) {
            return null;
        }
        return Long.parseLong(var1);
    }

    @Override
    public String convertFrom(Long var1, Type<String> type, MappingContext mappingContext) {
        if (var1 == null) {
            return null;
        }
        return String.valueOf(var1);
    }
}
