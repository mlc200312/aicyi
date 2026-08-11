package io.github.aicyi.commons.core.lock;

/**
 * @author Mr.Min
 * @description 分布式锁管理器
 * @date 2026/5/26
 **/
public interface DistributedLockManager {

    DistributedLock getLock(String name);
}