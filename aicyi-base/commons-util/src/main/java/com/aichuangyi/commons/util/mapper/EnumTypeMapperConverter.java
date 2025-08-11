package com.aichuangyi.commons.util.mapper;

import com.aichuangyi.core.EnumType;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * @author Mr.Min
 * @description 枚举类型自定义映射转换器
 * @date 11:35
 **/
public class EnumTypeMapperConverter extends BidirectionalConverter<EnumType, Integer> {


    @Override
    public Integer convertTo(EnumType enumType, Type<Integer> type, MappingContext mappingContext) {
        return enumType.getCode();
    }

    @Override
    public EnumType convertFrom(Integer code, Type<EnumType> type, MappingContext mappingContext) {
        for (EnumType enumConstant : type.getRawType().getEnumConstants()) {
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
