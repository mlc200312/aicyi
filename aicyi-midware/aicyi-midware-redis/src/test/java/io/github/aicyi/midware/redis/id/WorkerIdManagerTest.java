package io.github.aicyi.midware.redis.id;

import io.github.aicyi.commons.core.id.WorkerIdAllocator;
import io.github.aicyi.commons.lang.model.WorkerIdLease;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * WorkerIdManager 单元测试
 * <p>
 * 重点回归：幂等启动（重复 start 先停旧心跳）、停止后租约状态复位
 */
class WorkerIdManagerTest {

    private WorkerIdManager manager;

    @AfterEach
    void tearDown() {
        if (manager != null && manager.isRunning()) {
            manager.stop();
        }
    }

    @Test
    void startIsIdempotentAndStopResetsState() {

        WorkerIdAllocator allocator = mock(WorkerIdAllocator.class);
        when(allocator.allocate()).thenReturn(new WorkerIdLease(1, "token-1", 30));

        manager = new WorkerIdManager(allocator, 10L, false);

        manager.start();
        assertTrue(manager.isLeaseValid());
        assertTrue(manager.isRunning());

        // 重复启动：重新申请租约，不泄漏旧心跳
        manager.start();
        verify(allocator, times(2)).allocate();
        assertTrue(manager.isLeaseValid());

        manager.stop();
        assertFalse(manager.isLeaseValid());
        assertFalse(manager.isRunning());
        verify(allocator, atLeastOnce()).release(any(WorkerIdLease.class));
    }
}
