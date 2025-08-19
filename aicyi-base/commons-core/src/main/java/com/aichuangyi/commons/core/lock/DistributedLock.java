package com.aichuangyi.commons.core.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 分布式锁接口定义
 * @date 2025/8/18
 **/
public interface DistributedLock {

    /**
     * 获取锁，如果锁不可用则一直等待
     *
     * @throws InterruptedException 如果线程被中断
     */
    void lock() throws InterruptedException;

    /**
     * 尝试获取锁，获取成功返回true，失败返回false
     *
     * @return 是否获取到锁
     */
    boolean tryLock();

    /**
     * 尝试获取锁，在指定时间内获取不到则返回false
     *
     * @param time 等待时间
     * @param unit 时间单位
     * @return 是否获取到锁
     * @throws InterruptedException 如果线程被中断
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * 释放锁
     */
    void unlock();

    /**
     * 检查锁是否被当前线程持有
     *
     * @return 是否持有锁
     */
    boolean isHeldByCurrentThread();
}
