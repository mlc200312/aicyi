package io.github.aicyi.commons.core;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 统一的 Json 接口定义
 * @date 2019-05-22
 **/
public interface JsonCodec {
    String EMPTY_JSON_ARRAY = "[]";

    String EMPTY_JSON_OBJECT = "{}";

    /**
     * Serialize object to JSON string.
     */
    String toJson(Object value);

    /**
     * Deserialize JSON to target type.
     */
    <T> T fromJson(String json, Type type);

    /**
     * Deserialize JSON to target class.
     */
    default <T> T fromJson(String json, Class<T> type) {
        return fromJson(json, (Type) type);
    }

    /**
     * Deserialize JSON array to list.
     */
    default <E> List<E> fromJsonList(String json, Type elementType) {
        return fromJson(json, createCollectionType(List.class, elementType));
    }

    /**
     * Deserialize JSON object to map.
     */
    default <K, V> Map<K, V> fromJsonMap(String json,
                                         Type keyType,
                                         Type valueType) {
        return fromJson(json, createMapType(Map.class, keyType, valueType));
    }

    /**
     * Create simple type.
     */
    Type createType(Class<?> type);

    /**
     * Create parameterized type.
     */
    Type createParameterizedType(Class<?> rawType,
                                 Type... actualTypes);

    /**
     * Create collection type.
     */
    default Type createCollectionType(
            Class<? extends Collection> collectionType,
            Type elementType) {

        return createParameterizedType(collectionType, elementType);
    }

    /**
     * Create map type.
     */
    default Type createMapType(
            Class<? extends Map> mapType,
            Type keyType,
            Type valueType) {

        return createParameterizedType(mapType, keyType, valueType);
    }

    /**
     * Whether JSON is empty.
     */
    default boolean isEmptyJson(String json) {

        if (json == null || json.trim().isEmpty()) {
            return true;
        }

        String value = json.trim();

        return EMPTY_JSON_ARRAY.equals(value)
                || EMPTY_JSON_OBJECT.equals(value);
    }
}
