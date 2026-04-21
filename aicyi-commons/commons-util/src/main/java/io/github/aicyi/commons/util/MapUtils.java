package io.github.aicyi.commons.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description Map工具类
 * @date 2026/4/21
 **/
public class MapUtils {

    private MapUtils() {
    }

    public static <K, V> Map<V, K> reverse(Map<K, V> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }
        return source.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue,
                        Map.Entry::getKey,
                        (oldValue, newValue) -> newValue
                ));
    }
}