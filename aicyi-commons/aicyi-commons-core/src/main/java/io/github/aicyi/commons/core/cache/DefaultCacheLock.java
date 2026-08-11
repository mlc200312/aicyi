package io.github.aicyi.commons.core.cache;

import io.github.aicyi.commons.core.lock.DistributedLockManager;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 分布式缓存锁
 * @date 14:13
 **/
public class DefaultCacheLock implements CacheLock {

    private DistributedLockManager distributedLockManager;

    public DefaultCacheLock(DistributedLockManager distributedLockManager) {
        this.distributedLockManager = distributedLockManager;
    }

    @Override
    public boolean tryLock(String key, Duration ttl) {
        try {
            return distributedLockManager.getLock(key).tryLock(ttl);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        distributedLockManager.getLock(key).unlock();
    }
}
