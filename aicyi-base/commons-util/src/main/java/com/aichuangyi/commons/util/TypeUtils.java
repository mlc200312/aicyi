package com.aichuangyi.commons.util;

import org.apache.commons.collections4.SetUtils;

import java.util.Set;

public class TypeUtils {

    private static final Set<Class<?>> PRIMITIVE_TYPES = SetUtils.hashSet(
            byte.class, short.class, int.class, long.class,
            float.class, double.class, char.class, boolean.class
    );

    private static final Set<Class<?>> WRAPPER_TYPES = SetUtils.hashSet(
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, Character.class, Boolean.class,
            Void.class
    );

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return isPrimitive(clazz) || isWrapperType(clazz);
    }

    public static Class<?> wrapPrimitiveType(Class<?> clazz) {
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

        throw new IllegalArgumentException("未知的基本类型: " + clazz);
    }

    public static Class<?> unwrapWrapperType(Class<?> clazz) {
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

        throw new IllegalArgumentException("未知的包装类型: " + clazz);
    }
}