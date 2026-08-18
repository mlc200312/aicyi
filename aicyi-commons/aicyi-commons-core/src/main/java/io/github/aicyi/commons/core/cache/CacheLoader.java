package io.github.aicyi.commons.core.cache;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 缓存加载器接口
 * @date 2026/5/22
 **/
@FunctionalInterface
public interface CacheLoader<K, V> {

    V load(K key);

    /**
     * 批量加载，供 {@link Cache#getAll(Collection, CacheLoader)} 回填缺失 key。
     * 默认逐个 {@link #load(Object)}，实现方可覆写为真正的批量查询
     */
    default Map<K, V> loadAll(Collection<K> keys) {
        Map<K, V> result = new LinkedHashMap<>(keys.size());

        for (K key : keys) {
            result.put(key, load(key));
        }

        return result;
    }
}