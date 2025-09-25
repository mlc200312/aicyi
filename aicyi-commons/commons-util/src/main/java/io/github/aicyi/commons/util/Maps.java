package io.github.aicyi.commons.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Map 构造器
 */
public final class Maps {
    private Maps() {
    }

    public static <K, V> MapBuilder<K, V> of(K key, V value) {
        return (new HashMapBuilder()).and(key, value);
    }

    private static class HashMapBuilder<K, V> implements MapBuilder<K, V> {
        private final Map<K, V> data;

        private HashMapBuilder() {
            this.data = new HashMap();
        }

        public MapBuilder<K, V> and(K key, V value) {
            this.data.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return Collections.unmodifiableMap(this.data);
        }
    }

    public interface MapBuilder<K, V> {
        MapBuilder<K, V> and(K var1, V var2);

        Map<K, V> build();
    }
}