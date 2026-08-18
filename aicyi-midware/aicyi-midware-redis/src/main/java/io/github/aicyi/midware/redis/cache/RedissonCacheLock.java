package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.CacheLock;
import io.github.aicyi.commons.core.cache.DistributedCacheLock;
import io.github.aicyi.midware.redis.lock.RedissonDistributedLockManager;
import org.redisson.api.RedissonClient;

/**
 * @author Mr.Min
 * @description Redisson 实现的缓存防击穿锁（构造器快捷入口）
 * @date 14:36
 **/
public class RedissonCacheLock extends DistributedCacheLock implements CacheLock {

    public RedissonCacheLock(RedissonClient redissonClient) {
        super(new RedissonDistributedLockManager(redissonClient));
    }
}
