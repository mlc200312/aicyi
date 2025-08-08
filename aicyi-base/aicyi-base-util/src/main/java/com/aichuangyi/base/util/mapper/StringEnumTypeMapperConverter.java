package com.aichuangyi.base.util.mapper;

import com.aichuangyi.base.core.StringEnumType;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * @author Mr.Min
 * @description 字符串枚举类型自定义映射转换器
 * @date 11:35
 **/
public class StringEnumTypeMapperConverter extends BidirectionalConverter<StringEnumType, String> {

    @Override
    public String convertTo(StringEnumType enumType, Type<String> type, MappingContext mappingContext) {
        return enumType.getCode();
    }

    @Override
    public StringEnumType convertFrom(String code, Type<StringEnumType> type, MappingContext mappingContext) {
        for (StringEnumType enumConstant : type.getRawType().getEnumConstants()) {
            if (enumConstant.getCode() == code) {
                return enumConstant;
            }
        }
        return null;
    }

    @Override
    public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
        return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType) || this.destinationType.equals(sourceType) && this.sourceType.isAssignableFrom(destinationType);
    }
}
