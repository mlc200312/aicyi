package com.aichuangyi.commons.util.json;

import com.aichuangyi.core.EnumType;
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
 * @description 枚举类型反序列化
 * @date 2023/8/10
 **/
public class EnumTypeJsonDeserializer<E extends Enum<E> & EnumType> extends JsonDeserializer<E> implements ContextualDeserializer {
    private final Class<?> enumClazz;

    public EnumTypeJsonDeserializer() {
        this.enumClazz = this.getRawClass();
    }

    public EnumTypeJsonDeserializer(Class<?> enumClazz) {
        this.enumClazz = enumClazz;
    }

    @Override
    public E deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (enumClazz.isEnum() && EnumType.class.isAssignableFrom(enumClazz)) {
            int code = jsonParser.getValueAsInt();
            Class<E> clazz = (Class<E>) enumClazz;
            return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.getCode() == code).findAny().orElse(null);
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        Class<?> rawClass = deserializationContext.getContextualType().getRawClass();
        return new EnumTypeJsonDeserializer<>(rawClass);
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
