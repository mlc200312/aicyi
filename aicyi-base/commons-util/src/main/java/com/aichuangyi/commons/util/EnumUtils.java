package com.aichuangyi.commons.util;

import com.aichuangyi.commons.EnumType;
import com.aichuangyi.commons.StringEnumType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 枚举工具类
 * @date 2025/8/8
 **/
public class EnumUtils extends org.apache.commons.lang3.EnumUtils {

    public static <E extends Enum<?>> E valueOf(Class<E> enumClass, Object value, Method method) {
        E[] es = enumClass.getEnumConstants();
        for (E e : es) {
            Object evalue;
            try {
                method.setAccessible(true);
                evalue = method.invoke(e);
            } catch (InvocationTargetException | IllegalAccessException var10) {
                throw new IllegalArgumentException("Error: NoSuchMethod in " + enumClass.getName() + ".  Cause:", var10);
            }
            if (value instanceof Number && evalue instanceof Number && (new BigDecimal(String.valueOf(value))).compareTo(new BigDecimal(String.valueOf(evalue))) == 0) {
                return e;
            }
            if (Objects.equals(evalue, value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 获取枚举类
     *
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    public static <E extends Enum<?> & EnumType> E getType(Class<E> enumClass, int code) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.getCode() == code).findAny().orElse(null);
    }

    /**
     * 获取字符串枚举类
     *
     * @param enumClass
     * @param code
     * @param <E>
     * @return
     */
    public static <E extends Enum<?> & StringEnumType> E getType(Class<E> enumClass, String code) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.getCode().equals(code)).findAny().orElse(null);
    }

    /**
     * 判断是否相同
     *
     * @param type1
     * @param type2
     * @return
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
     * 判断是否相同
     *
     * @param type1
     * @param type2
     * @return
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
