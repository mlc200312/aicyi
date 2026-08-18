package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.CacheLockHandle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * RedisCacheLock 单元测试
 * <p>
 * 重点回归：每个句柄持有独立凭证，解锁时使用各自的凭证
 */
class RedisCacheLockTest {

    private StringRedisTemplate template;
    private ValueOperations<String, String> valueOps;
    private RedisCacheLock lock;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        template = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        lock = new RedisCacheLock(template);
    }

    @Test
    void handlesKeepDistinctValues() {

        // 记录每次加锁使用的随机值
        List<String> issued = new ArrayList<>();
        doAnswer(inv -> {
            issued.add(inv.getArgument(1));
            return true;
        }).when(valueOps).setIfAbsent(anyString(), anyString(), any(Duration.class));

        // 记录解锁脚本收到的校验值
        AtomicReference<Object> unlockedValue = new AtomicReference<>();
        doAnswer(inv -> {
            unlockedValue.set(inv.getArgument(2));
            return 1L;
        }).when(template).execute(any(RedisScript.class), anyList(), any());

        CacheLockHandle handleA = lock.tryLock("lock:a", Duration.ofSeconds(10));
        CacheLockHandle handleB = lock.tryLock("lock:b", Duration.ofSeconds(10));
        assertNotNull(handleA);
        assertNotNull(handleB);

        // 解锁 a 必须使用 a 自己的凭证
        handleA.unlock();
        assertEquals(issued.get(0), unlockedValue.get());

        // 随后解锁 b 仍使用 b 自己的凭证
        handleB.unlock();
        assertEquals(issued.get(1), unlockedValue.get());
    }

    @Test
    void handleWorksWithTryWithResources() {

        AtomicReference<Object> unlockedValue = new AtomicReference<>();
        doAnswer(inv -> {
            unlockedValue.set(inv.getArgument(2));
            return 1L;
        }).when(template).execute(any(RedisScript.class), anyList(), any());

        try (CacheLockHandle handle = lock.tryLock("lock:a", Duration.ofSeconds(10))) {
            assertNotNull(handle);
        }

        // close() 触发 unlock，脚本被执行且携带凭证
        assertNotNull(unlockedValue.get());
    }

    @Test
    void tryLockFailureReturnsNull() {
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

        assertNull(lock.tryLock("lock:a", Duration.ofSeconds(10)));
    }
}
