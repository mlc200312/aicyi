package io.github.aicyi.commons.util.enums;

import io.github.aicyi.commons.lang.EnumType;
import io.github.aicyi.commons.lang.StringEnumType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Mr.Min
 * @description 枚举工具类（继承自 lang3 无语义，已移除）
 * @date 2025/8/8
 **/
public final class EnumUtils {

    /**
     * 枚举常量缓存：枚举值集有限且不变，按类缓存避免高频调用重复反射取值
     */
    private static final ConcurrentMap<Class<?>, Object[]> CONSTANTS_CACHE = new ConcurrentHashMap<>();

    private EnumUtils() {
    }

    private static <E extends Enum<?>> E[] constants(Class<E> enumClass) {
        @SuppressWarnings("unchecked")
        E[] constants = (E[]) CONSTANTS_CACHE.computeIfAbsent(enumClass, Class::getEnumConstants);
        return constants;
    }

    /**
     * 按指定方法的返回值查找匹配的枚举项
     *
     * @param enumClass 枚举类
     * @param value     待匹配值（Number 类型按数值相等比较）
     * @param method    枚举项上用于取比较值的方法（如 getCode）
     * @return 匹配的枚举项，未命中返回 null
     */
    public static <E extends Enum<?>> E valueOf(Class<E> enumClass, Object value, Method method) {
        for (E e : constants(enumClass)) {
            Object obj;
            try {
                method.setAccessible(true);
                obj = method.invoke(e);
            } catch (InvocationTargetException | IllegalAccessException ex) {
                throw new IllegalArgumentException("invoke [" + method.getName() + "] failed on " + enumClass.getName(), ex);
            }
            if (value instanceof Number && obj instanceof Number && (new BigDecimal(String.valueOf(value))).compareTo(new BigDecimal(String.valueOf(obj))) == 0) {
                return e;
            }
            if (Objects.equals(obj, value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 按整型 code 查找枚举项
     *
     * @param enumClass 枚举类
     * @param code      枚举代码
     * @param <E>       枚举类型
     * @return 匹配的枚举项，未命中返回 null
     */
    public static <E extends Enum<?> & EnumType> E getType(Class<E> enumClass, int code) {
        return Arrays.stream(constants(enumClass)).filter(e -> e.getCode() == code).findAny().orElse(null);
    }

    /**
     * 按字符串 code 查找枚举项
     *
     * @param enumClass 枚举类
     * @param code      枚举代码
     * @param <E>       枚举类型
     * @return 匹配的枚举项，未命中返回 null
     */
    public static <E extends Enum<?> & StringEnumType> E getType(Class<E> enumClass, String code) {
        return Arrays.stream(constants(enumClass)).filter(e -> Objects.equals(code, e.getCode())).findAny().orElse(null);
    }

    /**
     * 按 code 判断两个整型枚举是否相同
     *
     * @param type1 枚举项，可为 null
     * @param type2 枚举项，可为 null
     * @return code 相同返回 true；任一为 null 返回 false
     */
    public static boolean equals(EnumType type1, EnumType type2) {
        if (type1 == type2) {
            return true;
        }
        if (type1 == null || type2 == null) {
            return false;
        }
        return type1.getCode().equals(type2.getCode());
    }

    /**
     * 按 code 判断两个字符串枚举是否相同
     *
     * @param type1 枚举项，可为 null
     * @param type2 枚举项，可为 null
     * @return code 相同返回 true；任一为 null 返回 false
     */
    public static boolean equals(StringEnumType type1, StringEnumType type2) {
        if (type1 == type2) {
            return true;
        }
        if (type1 == null || type2 == null) {
            return false;
        }
        return type1.getCode().equals(type2.getCode());
    }
}
