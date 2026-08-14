package io.github.aicyi.midware.redis.cache;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RedisCacheLock 单元测试
 * <p>
 * 重点回归：同一线程持有多个锁时，锁值不能互相覆盖
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
    void multipleLocksKeepDistinctValues() {

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

        assertTrue(lock.tryLock("lock:a", Duration.ofSeconds(10)));
        assertTrue(lock.tryLock("lock:b", Duration.ofSeconds(10)));

        // 解锁 a 必须使用 a 自己的值，而不是被 b 覆盖
        lock.unlock("lock:a");
        assertEquals(issued.get(0), unlockedValue.get());

        // 随后解锁 b 仍能使用 b 自己的值
        lock.unlock("lock:b");
        assertEquals(issued.get(1), unlockedValue.get());
    }

    @Test
    void unlockWithoutHoldingDoesNothing() {
        lock.unlock("lock:a");

        verify(template, never()).execute(any(RedisScript.class), anyList(), any());
    }
}
