package io.github.aicyi.commons.util;

import io.github.aicyi.commons.lang.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mr.Min
 * @description 类型工具类
 * @date 2026/4/27
 **/
public final class TypeUtils {

    private TypeUtils() {
    }

    private static final Set<Class<?>> WRAPPER_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, Character.class, Boolean.class,
            Void.class
    )));

    public static boolean isPrimitive(Class<?> clazz) {
        Assert.notNull(clazz, "clazz");
        return clazz.isPrimitive();
    }

    public static boolean isWrapperType(Class<?> clazz) {
        Assert.notNull(clazz, "clazz");
        return WRAPPER_TYPES.contains(clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return isPrimitive(clazz) || isWrapperType(clazz);
    }

    public static Class<?> wrapPrimitiveType(Class<?> clazz) {
        Assert.notNull(clazz, "clazz");
        if (!clazz.isPrimitive()) return clazz;

        if (clazz == int.class) return Integer.class;
        if (clazz == long.class) return Long.class;
        if (clazz == boolean.class) return Boolean.class;
        if (clazz == char.class) return Character.class;
        if (clazz == float.class) return Float.class;
        if (clazz == double.class) return Double.class;
        if (clazz == byte.class) return Byte.class;
        if (clazz == short.class) return Short.class;
        if (clazz == void.class) return Void.class;

        throw new IllegalArgumentException("unknown primitive type: " + clazz);
    }

    public static Class<?> unwrapWrapperType(Class<?> clazz) {
        Assert.notNull(clazz, "clazz");
        if (!WRAPPER_TYPES.contains(clazz)) return clazz;

        if (clazz == Integer.class) return int.class;
        if (clazz == Long.class) return long.class;
        if (clazz == Boolean.class) return boolean.class;
        if (clazz == Character.class) return char.class;
        if (clazz == Float.class) return float.class;
        if (clazz == Double.class) return double.class;
        if (clazz == Byte.class) return byte.class;
        if (clazz == Short.class) return short.class;
        if (clazz == Void.class) return void.class;

        throw new IllegalArgumentException("unknown wrapper type: " + clazz);
    }
}