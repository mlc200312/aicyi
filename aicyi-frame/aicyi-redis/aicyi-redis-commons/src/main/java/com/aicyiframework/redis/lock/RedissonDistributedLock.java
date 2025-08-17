package com.aicyiframework.redis.lock;

import com.aichuangyi.commons.core.lock.DistributedLock;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的分布式锁实现
 */
public class RedissonDistributedLock implements DistributedLock {

    private final RLock rLock;
    private final RedissonClient redissonClient;

    /**
     * 构造函数
     *
     * @param lockKey      锁的key
     * @param redisAddress Redis地址，格式如 "redis://127.0.0.1:6379"
     */
    public RedissonDistributedLock(String lockKey, String redisAddress) {
        Config config = new Config();
        config.useSingleServer().setAddress(redisAddress);
        this.redissonClient = Redisson.create(config);
        this.rLock = redissonClient.getLock(lockKey);
    }

    @Override
    public void lock() throws InterruptedException {
        rLock.lock();
    }

    @Override
    public boolean tryLock() {
        return rLock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return rLock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        if (rLock.isHeldByCurrentThread()) {
            rLock.unlock();
        }
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return rLock.isHeldByCurrentThread();
    }

    /**
     * 关闭Redisson客户端
     */
    public void shutdown() {
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
    }
}
