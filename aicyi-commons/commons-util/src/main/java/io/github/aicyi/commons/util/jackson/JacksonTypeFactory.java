package io.github.aicyi.commons.util.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author Mr.Min
 * @description Jackson JavaType 构建工具
 * @date 2026/4/28
 **/
public final class JacksonTypeFactory {

    private static final TypeFactory TYPE_FACTORY =
            new ObjectMapper().getTypeFactory();

    private JacksonTypeFactory() {
    }

    /**
     * 普通类型
     */
    public static <T> JavaType typeOf(Class<T> clazz) {
        return TYPE_FACTORY.constructType(clazz);
    }

    /**
     * TypeReference 类型
     */
    public static <T> JavaType typeOf(TypeReference<T> typeReference) {
        return TYPE_FACTORY.constructType(typeReference.getType());
    }

    /**
     * Type 类型
     */
    public static JavaType typeOf(Type type) {
        return TYPE_FACTORY.constructType(type);
    }

    /**
     * 集合类型
     */
    public static <C extends Collection<E>, E> JavaType collectionTypeOf(
            Class<C> collectionType,
            Class<E> elementType) {

        return TYPE_FACTORY.constructCollectionType(collectionType, elementType);
    }

    /**
     * Map 类型
     */
    public static <M extends Map<K, V>, K, V> JavaType mapTypeOf(
            Class<M> mapType,
            Class<K> keyType,
            Class<V> valueType) {

        return TYPE_FACTORY.constructMapType(mapType, keyType, valueType);
    }

    /**
     * 参数化类型
     */
    public static JavaType parametricTypeOf(
            Class<?> rawType,
            JavaType... parameterTypes) {

        return TYPE_FACTORY.constructParametricType(rawType, parameterTypes);
    }
}