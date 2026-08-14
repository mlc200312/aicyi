package io.github.aicyi.midware.redis.lock;

import io.github.aicyi.commons.core.lock.DistributedLock;
import io.github.aicyi.commons.util.Assert;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 基于Redisson的分布式锁实现
 * @date 2025/8/18
 **/
public class RedissonDistributedLock implements DistributedLock {

    private final String name;
    private final RLock lock;

    public RedissonDistributedLock(String name, RedissonClient redissonClient) {
        this.name = name;
        this.lock = redissonClient.getLock(name);
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * 阻塞获取锁
     * <p>
     * 使用 Redisson watchdog 自动续租
     */
    @Override
    public void lock() throws InterruptedException {
        lock.lockInterruptibly();
    }

    /**
     * 阻塞获取锁（固定租约）
     * <p>
     * 不启用 watchdog
     */
    @Override
    public void lock(Duration leaseTime) throws InterruptedException {

        Assert.notNull(leaseTime, "leaseTime");

        validateDuration(leaseTime, "leaseTime");

        lock.lockInterruptibly(leaseTime.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 立即尝试获取
     */
    @Override
    public boolean tryLock() {
        try {
            return lock.tryLock();
        } catch (Exception e) {
            throw new LockException("Failed to acquire lock: " + name, e);
        }
    }

    /**
     * 等待指定时间尝试获取
     * <p>
     * leaseTime = -1 -> watchdog 模式
     */
    @Override
    public boolean tryLock(Duration waitTime) throws InterruptedException {
        Assert.notNull(waitTime, "waitTime");

        validateDuration(waitTime, "waitTime");

        return lock.tryLock(waitTime.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 指定等待时间 + 固定租约
     */
    @Override
    public boolean tryLock(Duration waitTime, Duration leaseTime) throws InterruptedException {
        Assert.notNull(waitTime, "waitTime");
        Assert.notNull(leaseTime, "leaseTime");

        validateDuration(waitTime, "waitTime");
        validateDuration(leaseTime, "leaseTime");

        return lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 释放锁
     * <p>
     * Redisson 语义：
     * 当前线程不是持有者 -> IllegalMonitorStateException
     */
    @Override
    public void unlock() {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException e) {
            throw new LockException("Current thread does not hold lock: " + name, e);
        } catch (Exception e) {
            throw new LockException("Failed to release lock: " + name, e);
        }
    }

    /**
     * 当前线程是否持有锁
     */
    @Override
    public boolean isHeldByCurrentThread() {
        try {
            return lock.isHeldByCurrentThread();
        } catch (Exception e) {
            throw new LockException("Failed to check lock owner: " + name, e);
        }
    }

    /**
     * 锁是否被占用
     */
    @Override
    public boolean isLocked() {
        try {
            return lock.isLocked();
        } catch (Exception e) {
            throw new LockException("Failed to check lock state: " + name, e);
        }
    }

    /**
     * 管理员强制释放
     */
    @Override
    public boolean forceUnlock() {
        try {
            return lock.forceUnlock();
        } catch (Exception e) {
            throw new LockException("Failed to force unlock: " + name, e);
        }
    }

    private void validateDuration(Duration duration, String fieldName) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }

        if (duration.isZero()) {
            return;
        }

        if (duration.toMillis() <= 0) {
            throw new IllegalArgumentException(fieldName + " is too small");
        }
    }
}
