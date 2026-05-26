package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.CacheLock;
import io.github.aicyi.commons.core.cache.DefaultCacheLock;
import io.github.aicyi.midware.redis.lock.RedissonDistributedLockManager;
import org.redisson.api.RedissonClient;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 14:36
 **/
public class RedissonCacheLock extends DefaultCacheLock implements CacheLock {

    public RedissonCacheLock(RedissonClient redissonClient) {
        super(new RedissonDistributedLockManager(redissonClient));
    }
}
