package com.aichuangyi.base.util.mapper;

import com.aichuangyi.base.lang.BaseBean;
import com.aichuangyi.base.util.json.JsonUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * @author Mr.Min
 * @description Json 类型自定义映射转换器
 * @date 11:35
 **/
public class JsonMapperConverter extends BidirectionalConverter<BaseBean, String> {

    @Override
    public String convertTo(BaseBean source, Type<String> type, MappingContext mappingContext) {
        return JsonUtils.getInstance().toJson(source);
    }

    @Override
    public BaseBean convertFrom(String source, Type<BaseBean> type, MappingContext mappingContext) {
        return JsonUtils.getInstance().parse(source, type);
    }

    @Override
    public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
        return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType) || this.destinationType.equals(sourceType) && this.sourceType.isAssignableFrom(destinationType);
    }
}
