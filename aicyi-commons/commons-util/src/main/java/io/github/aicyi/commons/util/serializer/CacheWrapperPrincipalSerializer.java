package io.github.aicyi.commons.util.serializer;

import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.PrincipalSerializer;
import io.github.aicyi.commons.lang.model.CacheWrapper;
import io.github.aicyi.commons.util.Assert;
import io.github.aicyi.commons.util.JsonUtils;

import java.lang.reflect.Type;

/**
 * @author Mr.Min
 * @description CacheWrapper Principal序列化器
 * @date 2026/5/25
 **/
public class CacheWrapperPrincipalSerializer<T> implements PrincipalSerializer<CacheWrapper<T>> {

    private final JsonCodec jsonCodec;

    private final Type type;

    public CacheWrapperPrincipalSerializer(JsonCodec jsonCodec, Class<?> rawType, Type... actualTypes) {
        this.jsonCodec = jsonCodec;
        this.type = jsonCodec.createParameterizedType(rawType, actualTypes);
    }

    public CacheWrapperPrincipalSerializer(Class<?> rawType, Type... actualTypes) {
        this(JsonUtils.getInstance(), rawType, actualTypes);
    }


    @Override
    public String serialize(CacheWrapper<T> cacheWrapper) {
        try {

            return jsonCodec.toJson(cacheWrapper);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public CacheWrapper<T> deserialize(String value) {

        Assert.notNull(value, "value");

        try {

            Type parameterizedType = jsonCodec.createParameterizedType(CacheWrapper.class, type);

            return jsonCodec.fromJson(value, parameterizedType);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}