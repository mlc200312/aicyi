package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.CacheLoader;
import io.github.aicyi.commons.core.cache.CacheLock;
import io.github.aicyi.commons.core.cache.CacheLockHandle;
import io.github.aicyi.commons.core.cache.CacheWrapper;
import io.github.aicyi.commons.util.codec.CacheWrapperCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RedisCache 行为正确性测试（mock StringRedisTemplate，不依赖真实 Redis）
 */
class RedisCacheTest {

    private StringRedisTemplate template;
    private ValueOperations<String, String> valueOps;
    private CacheLock lock;
    private CacheWrapperCodec<String> serializer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        template = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(valueOps);
        lock = mock(CacheLock.class);
        serializer = new CacheWrapperCodec<>(String.class);
    }

    private RedisCacheConfig config(boolean cacheNull) {
        return RedisCacheConfig.builder()
                .globalPrefix("p")
                .cacheName("c")
                .ttl(Duration.ofMinutes(1))
                .cacheNull(cacheNull)
                .build();
    }

    /**
     * 修复点1：getAll 遇到空值占位（data=null）不再抛 NPE，占位以 null 值返回，miss 被排除
     */
    @Test
    void getAllToleratesNullValuePlaceholder() {
        RedisCache<String> cache = new RedisCache<>(template, config(true), serializer, lock);

        String hitValue = serializer.serialize(CacheWrapper.hit("v"));
        String nullPlaceholder = serializer.serialize(CacheWrapper.<String>hit(null));

        when(valueOps.multiGet(anyList())).thenReturn(Arrays.asList(hitValue, nullPlaceholder, null));

        Map<String, String> result = cache.getAll(Arrays.asList("a", "b", "c"));

        assertEquals(2, result.size());
        assertEquals("v", result.get("a"));
        assertTrue(result.containsKey("b"));
        assertNull(result.get("b"));
        assertFalse(result.containsKey("c"));
    }

    /**
     * 修复点2：cacheNull=false 时 put(null) 不写入占位 key
     */
    @Test
    void putNullSkipsWriteWhenCacheNullDisabled() {
        RedisCache<String> cache = new RedisCache<>(template, config(false), serializer, lock);

        cache.put("k", null);

        verify(valueOps, never()).set(anyString(), anyString());
        verify(valueOps, never()).set(anyString(), anyString(), any(Duration.class));
    }

    /**
     * 修复点2：cacheNull=false 时 putAll 跳过 null 值，仅写入非空项
     */
    @Test
    @SuppressWarnings("unchecked")
    void putAllSkipsNullValuesWhenCacheNullDisabled() {
        RedisCache<String> cache = new RedisCache<>(template, config(false), serializer, lock);

        Map<String, String> values = new LinkedHashMap<>();
        values.put("a", "1");
        values.put("b", null);

        cache.putAll(values, null);

        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(valueOps).multiSet(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    /**
     * 修复点3：亚秒 TTL 经 jitter 后不被截断为 0
     */
    @Test
    void subSecondTtlNotTruncatedByJitter() {
        RedisCacheConfig cfg = RedisCacheConfig.builder()
                .globalPrefix("p")
                .cacheName("c")
                .ttl(Duration.ofMillis(500))
                .build();
        RedisCache<String> cache = new RedisCache<>(template, cfg, serializer, lock);

        cache.put("k", "v");

        ArgumentCaptor<Duration> captor = ArgumentCaptor.forClass(Duration.class);
        verify(valueOps).set(eq("p:c:k"), anyString(), captor.capture());
        assertTrue(captor.getValue().toMillis() >= 500);
    }

    /**
     * 修复点4：unlock 抛异常（如 Redisson 租约过期）不掩盖 loader 的正常返回值
     */
    @Test
    void unlockFailureDoesNotMaskLoaderResult() {
        CacheLockHandle handle = mock(CacheLockHandle.class);
        when(lock.tryLock(anyString(), any(Duration.class))).thenReturn(handle);
        doThrow(new IllegalStateException("lease expired")).when(handle).unlock();
        when(valueOps.get(anyString())).thenReturn(null);

        RedisCache<String> cache = new RedisCache<>(template, config(true), serializer, lock);

        assertEquals("v", cache.get("k", key -> "v"));
    }

    /**
     * 修复点6（防击穿收敛）：等待超时且抢不到锁时不执行加载，穿透返回 null
     */
    @Test
    void waiterDoesNotLoadWhenLockHeldByOther() {
        when(lock.tryLock(anyString(), any(Duration.class))).thenReturn(null);
        when(valueOps.get(anyString())).thenReturn(null);

        RedisCacheConfig cfg = RedisCacheConfig.builder()
                .globalPrefix("p")
                .cacheName("c")
                .ttl(Duration.ofMinutes(1))
                .waitTimeout(Duration.ofMillis(10))
                .build();

        RedisCache<String> cache = new RedisCache<>(template, cfg, serializer, lock);

        AtomicInteger loadCount = new AtomicInteger();

        String result = cache.get("k", key -> {
            loadCount.incrementAndGet();
            return "v";
        });

        assertNull(result);
        assertEquals(0, loadCount.get());
    }

    /**
     * 能力增强：getAll(keys, loader) 批量回填缺失 key
     */
    @Test
    void getAllWithLoaderBackfillsMissingKeys() {
        RedisCache<String> cache = new RedisCache<>(template, config(true), serializer, lock);

        String hitValue = serializer.serialize(CacheWrapper.hit("v1"));

        when(valueOps.multiGet(anyList())).thenReturn(Arrays.asList(hitValue, null));

        Map<String, String> result = cache.getAll(Arrays.asList("a", "b"), new CacheLoader<String, String>() {
            @Override
            public String load(String key) {
                return "loaded-" + key;
            }
        });

        assertEquals(2, result.size());
        assertEquals("v1", result.get("a"));
        assertEquals("loaded-b", result.get("b"));
    }

    /**
     * 修复点5：零值 ttl/lockTtl/waitTimeout 在构建期即被拒绝
     */
    @Test
    void configRejectsZeroDurations() {
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                RedisCacheConfig.builder()
                        .globalPrefix("p")
                        .cacheName("c")
                        .ttl(Duration.ZERO)
                        .build());
    }
}
