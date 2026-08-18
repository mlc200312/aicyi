package io.github.aicyi.commons.util;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * @author Mr.Min
 * @description UUID 工具类
 * @date 18:28
 **/
public final class UUIDUtils {

    private UUIDUtils() {
    }

    public static String generateV7Id() {
        return UuidCreator.getTimeOrderedWithRandom().toString().replace("-", "");
    }
}
