package com.aichuangyi.commons.util.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 获取Jackson的JavaType
 * @date 2025/8/19
 **/
public class JacksonHelper {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeFactory typeFactory = mapper.getTypeFactory();

    /**
     * 获取普通类型的JavaType
     */
    public static <T> JavaType getType(Class<T> clazz) {
        return typeFactory.constructType(clazz);
    }

    /**
     * 通过TypeReference获取复杂泛型类型
     */
    public static <T> JavaType getType(TypeReference<T> typeReference) {
        return typeFactory.constructType(typeReference.getType());
    }

    /**
     * 通过Type对象获取JavaType
     */
    public static JavaType getType(Type type) {
        return typeFactory.constructType(type);
    }

    /**
     * 获取集合类型的JavaType
     */
    public static <C extends Collection<E>, E> JavaType getCollectionType(Class<C> collectionClass, Class<E> elementClass) {
        return typeFactory.constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 获取Map类型的JavaType
     */
    public static <M extends Map<K, V>, K, V> JavaType getMapType(Class<M> mapClass, Class<K> keyClass, Class<V> valueClass) {
        return typeFactory.constructMapType(mapClass, keyClass, valueClass);
    }

    /**
     * 获取参数化类型的JavaType
     */
    public static JavaType getParametricType(Class<?> parametrized, JavaType... parameterTypes) {
        return typeFactory.constructParametricType(parametrized, parameterTypes);
    }
}