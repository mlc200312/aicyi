package io.github.aicyi.commons.util.serializer;

import io.github.aicyi.commons.core.codec.JsonCodec;
import io.github.aicyi.commons.core.cache.CacheWrapper;
import io.github.aicyi.commons.core.codec.StringCodec;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.commons.util.JsonUtils;

import java.lang.reflect.Type;

/**
 * @author Mr.Min
 * @description CacheWrapper 的 JSON 编解码器（缓存值存储格式）
 * @date 2026/5/25
 **/
public class CacheWrapperCodec<T> implements StringCodec<CacheWrapper<T>> {

    private final JsonCodec jsonCodec;

    private final Type type;

    public CacheWrapperCodec(JsonCodec jsonCodec, Class<?> rawType, Type... actualTypes) {
        this.jsonCodec = jsonCodec;
        this.type = jsonCodec.createParameterizedType(rawType, actualTypes);
    }

    public CacheWrapperCodec(Class<?> rawType, Type... actualTypes) {
        this(JsonUtils.getInstance(), rawType, actualTypes);
    }


    @Override
    public String serialize(CacheWrapper<T> cacheWrapper) {
        return jsonCodec.toJson(cacheWrapper);
    }

    @Override
    public CacheWrapper<T> deserialize(String value) {

        Assert.notNull(value, "value");

        Type parameterizedType = jsonCodec.createParameterizedType(CacheWrapper.class, type);

        return jsonCodec.fromJson(value, parameterizedType);
    }
}