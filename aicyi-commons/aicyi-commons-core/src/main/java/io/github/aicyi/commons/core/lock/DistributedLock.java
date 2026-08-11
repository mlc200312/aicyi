package io.github.aicyi.commons.core.lock;


import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * 分布式锁
 * <p>
 * 一个 DistributedLock 实例绑定一个唯一资源。
 * <p>
 * 语义：
 * - lock：阻塞直到获取成功
 * - tryLock：按条件尝试获取
 * - unlock：仅当前持有者可释放
 */
public interface DistributedLock {

    /**
     * 锁名称（资源标识）
     */
    String name();

    /**
     * 阻塞获取锁（默认策略）
     * <p>
     * 默认由实现决定：
     * - 是否自动续租
     * - 默认租约时间
     */
    void lock() throws InterruptedException;

    /**
     * 阻塞获取锁（指定租约时间）
     * <p>
     * leaseTime 为 null 表示实现默认行为（例如 watchdog）
     */
    void lock(Duration leaseTime) throws InterruptedException;

    /**
     * 立即尝试获取锁
     *
     * @return true 获取成功
     */
    boolean tryLock();

    /**
     * 指定等待时间尝试获取锁
     *
     * @return true 获取成功
     */
    boolean tryLock(Duration waitTime) throws InterruptedException;

    /**
     * 指定等待时间和租约时间尝试获取锁
     *
     * @return true 获取成功
     */
    boolean tryLock(Duration waitTime, Duration leaseTime) throws InterruptedException;

    /**
     * 释放锁
     * <p>
     * 如果当前调用线程不是持有者，应抛出异常
     */
    void unlock();

    /**
     * 当前线程是否持有锁
     */
    boolean isHeldByCurrentThread();

    /**
     * 锁是否被占用
     */
    boolean isLocked();

    /**
     * 强制释放（管理员能力）
     */
    boolean forceUnlock();

    // =========================
    // Template Methods
    // =========================

    /**
     * 在锁保护下执行任务
     */
    default void execute(Runnable task) throws InterruptedException {
        lock();
        try {
            task.run();
        } finally {
            unlock();
        }
    }

    /**
     * 在锁保护下执行任务并返回结果
     */
    default <T> T execute(Callable<T> task) throws Exception {
        lock();
        try {
            return task.call();
        } finally {
            unlock();
        }
    }

    /**
     * 尝试执行任务
     *
     * @return true 执行成功（获取锁并执行）
     */
    default boolean tryExecute(Duration waitTime, Runnable task) throws InterruptedException {
        boolean locked = tryLock(waitTime);
        if (!locked) {
            return false;
        }

        try {
            task.run();
            return true;
        } finally {
            unlock();
        }
    }

    /**
     * 尝试执行任务并返回结果
     */
    default <T> T tryExecute(
            Duration waitTime,
            Callable<T> task,
            T fallback
    ) throws Exception {
        boolean locked = tryLock(waitTime);
        if (!locked) {
            return fallback;
        }

        try {
            return task.call();
        } finally {
            unlock();
        }
    }
}
