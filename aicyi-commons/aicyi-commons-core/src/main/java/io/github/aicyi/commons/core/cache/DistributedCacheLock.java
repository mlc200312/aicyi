package io.github.aicyi.commons.core.cache;

import io.github.aicyi.commons.core.lock.DistributedLock;
import io.github.aicyi.commons.core.lock.DistributedLockManager;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 分布式缓存锁：委托 {@link DistributedLockManager} 实现，非无依赖默认实现；
 * 自包含的轻量实现见 Redis 模块的 RedisCacheLock
 * @date 14:13
 **/
public class DistributedCacheLock implements CacheLock {

    private final DistributedLockManager distributedLockManager;

    public DistributedCacheLock(DistributedLockManager distributedLockManager) {
        this.distributedLockManager = distributedLockManager;
    }

    /**
     * ttl 作为锁租约时间：零等待立即尝试，与 RedisCacheLock 的 setIfAbsent+TTL 语义对齐
     */
    @Override
    public CacheLockHandle tryLock(String key, Duration ttl) {
        DistributedLock lock = distributedLockManager.getLock(key);

        try {
            if (lock.tryLock(Duration.ZERO, ttl)) {
                // 静默解锁：锁可能已租约到期自动释放，unlock 失败不得在
                // try-with-resources 的 close 中掩盖业务返回值或原始异常
                return new CacheLockHandle() {
                    @Override
                    public void unlock() {
                        try {
                            lock.unlock();
                        } catch (Exception ignore) {
                            // 缓存防护锁为短租约尽力锁，释放失败仅意味着租约已到期
                        }
                    }
                };
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
