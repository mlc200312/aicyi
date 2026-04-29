package io.github.aicyi.commons.lang;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 统一的 Json 接口定义
 * @date 2019-05-22
 **/
public interface SmartJsonMapper {
    String EMPTY_ARRAY = "[]", EMPTY_OBJECT = "{}";

    /**
     * 构造简单type
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends Serializable> Type constructType(Class<T> clazz);

    /**
     * 构造type
     *
     * @param clazz
     * @param parameterTypes
     * @return
     */
    Type constructParametricType(Class<?> clazz, Type... parameterTypes);

    /**
     * 构造Collection Type
     *
     * @param collClazz
     * @param type
     * @return
     */
    default Type constructCollectionType(Class<? extends Collection> collClazz, Type type) {
        return constructParametricType(collClazz, type);
    }

    /**
     * 构造Map Type
     *
     * @param mapClazz
     * @param keyType
     * @param valueType
     * @return
     */
    default Type constructMapType(Class<? extends Map> mapClazz, Type keyType, Type valueType) {
        return constructParametricType(mapClazz, keyType, valueType);
    }

    /**
     * 对象转JSON
     *
     * @param object
     * @return
     */
    String toJson(Object object);

    /**
     * JSON转对象
     *
     * @param json
     * @return
     * @throws ParseException
     */
    default Object parse(String json) {
        try {
            return this.parse(json, Object.class);
        } catch (Throwable e) {
            throw new IllegalArgumentException("parse json error", e);
        }
    }

    /**
     * JSON转对象
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    <T> T parse(String json, Type type);

    /**
     * JSON转List
     *
     * @param json
     * @param <E>
     * @return
     */
    default <E> List<E> parseList(String json) {
        return this.parse(json, List.class);
    }

    /**
     * JSON转List
     *
     * @param json
     * @param type
     * @param <E>
     * @return
     */
    default <E> List<E> parseList(String json, Type type) {
        return this.parse(json, this.constructCollectionType(List.class, type));
    }

    /**
     * JSON转Map
     *
     * @param json
     * @return
     */
    default <K, V> Map<K, V> parseMap(String json) {
        return this.parse(json, Map.class);
    }

    /**
     * JSON转Map
     *
     * @param json
     * @param clazz
     * @param <V>
     * @return
     */
    default <V> Map<String, V> parseMap(String json, Class<V> clazz) {
        return this.parseMap(json, String.class, clazz);
    }

    /**
     * JSON转Map
     *
     * @param json
     * @param keyType
     * @param valueType
     * @param <K>
     * @param <V>
     * @return
     */
    default <K, V> Map<K, V> parseMap(String json, Type keyType, Type valueType) {
        return this.parse(json, constructMapType(Map.class, keyType, valueType));
    }

    /**
     * 判断空JSON
     *
     * @param json
     * @return
     */
    default boolean isEmptyJSON(String json) {
        if (json == null || json.trim().isEmpty()) {
            return true;
        }
        if (EMPTY_ARRAY.equals(json.trim()) || EMPTY_OBJECT.equals(json.trim())) {
            return true;
        }
        return false;
    }
}
