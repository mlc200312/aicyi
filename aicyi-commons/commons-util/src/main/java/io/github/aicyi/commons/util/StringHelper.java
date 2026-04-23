package io.github.aicyi.commons.util;

/**
 * @author Mr.Min
 * @description String帮助类
 * @date 2026/4/23
 **/
public class StringHelper {
    public static String getObjectValue(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}