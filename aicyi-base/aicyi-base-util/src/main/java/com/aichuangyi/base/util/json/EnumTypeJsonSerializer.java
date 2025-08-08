package com.aichuangyi.base.util.json;

import com.aichuangyi.base.core.EnumType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 2023/8/10
 **/
public class EnumTypeJsonSerializer<E extends Enum<E> & EnumType> extends com.fasterxml.jackson.databind.JsonSerializer<E> {

    @Override
    public void serialize(E e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(e.getCode());
    }
}
