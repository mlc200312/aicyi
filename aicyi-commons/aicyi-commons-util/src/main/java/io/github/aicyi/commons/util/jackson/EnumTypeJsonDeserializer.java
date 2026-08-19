package io.github.aicyi.commons.util.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.github.aicyi.commons.lang.EnumType;

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
            // 严格取值：字符串 token 不再被 getValueAsInt 静默转为 0，避免误命中 code=0 的枚举
            JsonToken token = jsonParser.currentToken();
            if (token != JsonToken.VALUE_NUMBER_INT) {
                deserializationContext.reportWrongTokenException(jsonParser,
                        JsonToken.VALUE_NUMBER_INT, "enum code must be an integer for %s", enumClazz.getName());
                // 不可达：reportWrongTokenException 必定抛出异常
                return null;
            }
            int code = jsonParser.getIntValue();
            Class<E> clazz = (Class<E>) enumClazz;
            return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.getCode() == code).findAny()
                    // 未知 code 抛异常而非静默返回 null，避免上游脏数据被掩盖
                    .orElseThrow(() -> new IllegalArgumentException(
                            "unknown enum code [" + code + "] for " + enumClazz.getName()));
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
