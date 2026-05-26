package io.github.aicyi.midware.redis.lock;

import io.github.aicyi.commons.core.lock.DistributedLock;
import io.github.aicyi.commons.core.lock.DistributedLockManager;
import org.redisson.api.RedissonClient;

/**
 * @author Mr.Min
 * @description Redisson 分布式锁管理器
 * @date 2026/5/26
 **/
public class RedissonDistributedLockManager implements DistributedLockManager {

    private final RedissonClient redissonClient;

    public RedissonDistributedLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public DistributedLock getLock(String name) {
        return new RedissonDistributedLock(name, redissonClient);
    }
}