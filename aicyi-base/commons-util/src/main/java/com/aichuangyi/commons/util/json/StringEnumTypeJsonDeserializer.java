package com.aichuangyi.commons.util.json;

import com.aichuangyi.commons.core.StringEnumType;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author Mr.Min
 * @description 字符串枚举反序列化
 * @date 2023/8/10
 **/
public class StringEnumTypeJsonDeserializer<E extends Enum<E> & StringEnumType> extends JsonDeserializer<E> implements ContextualDeserializer {
    private Class<?> enumClazz;

    public StringEnumTypeJsonDeserializer() {
        this.enumClazz = this.getRawClass();
    }

    public StringEnumTypeJsonDeserializer(Class<?> enumClazz) {
        this.enumClazz = enumClazz;
    }

    @Override
    public E deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (enumClazz.isEnum() && StringEnumType.class.isAssignableFrom(enumClazz)) {
            String code = jsonParser.getValueAsString();
            Class<E> clazz = (Class<E>) enumClazz;
            return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.getCode().equals(code)).findAny().orElse(null);
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        Class<?> rawClass = deserializationContext.getContextualType().getRawClass();
        return new StringEnumTypeJsonDeserializer<>(rawClass);
    }

    private Class<?> getRawClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        return null;
    }
}
