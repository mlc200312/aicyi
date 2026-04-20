package io.github.aicyi.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author Mr.Min
 * @description 数字工具类
 * @date 2026/4/20
 **/
public final class NumberUtils {

    private NumberUtils() {
    }

    /**
     * 判断是否为空
     */
    public static boolean isNull(Number number) {
        return number == null;
    }

    /**
     * 判断是否非空
     */
    public static boolean isNotNull(Number number) {
        return number != null;
    }

    /**
     * 判断是否大于0
     */
    public static boolean isPositive(Number number) {
        return number != null && toBigDecimal(number).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 判断是否大于等于0
     */
    public static boolean isNonNegative(Number number) {
        return number != null && toBigDecimal(number).compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * 判断是否小于0
     */
    public static boolean isNegative(Number number) {
        return number != null && toBigDecimal(number).compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 判断是否在指定区间（包含边界）
     */
    public static boolean between(Number value, Number min, Number max) {
        if (value == null || min == null || max == null) {
            return false;
        }

        BigDecimal val = toBigDecimal(value);
        BigDecimal minVal = toBigDecimal(min);
        BigDecimal maxVal = toBigDecimal(max);

        return val.compareTo(minVal) >= 0 && val.compareTo(maxVal) <= 0;
    }

    /**
     * 判断字符串是否为合法数字
     */
    public static boolean isNumeric(String str) {
        if (str == null || StringUtils.isBlank(str)) {
            return false;
        }

        try {
            new BigDecimal(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Number转BigDecimal
     */
    private static BigDecimal toBigDecimal(Number number) {
        return new BigDecimal(number.toString());
    }
}