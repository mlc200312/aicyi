package io.github.aicyi.commons.core.cache;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 缓存锁接口
 * @date 2026/5/22
 **/
public interface CacheLock {

    boolean tryLock(String key, Duration ttl);

    void unlock(String key);
}