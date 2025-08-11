package com.aichuangyi.commons.util.json;

import com.aichuangyi.commons.EnumType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Mr.Min
 * @description 枚举序列化
 * @date 2023/8/10
 **/
public class EnumTypeJsonSerializer<E extends Enum<E> & EnumType> extends com.fasterxml.jackson.databind.JsonSerializer<E> {

    @Override
    public void serialize(E e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(e.getCode());
    }
}
