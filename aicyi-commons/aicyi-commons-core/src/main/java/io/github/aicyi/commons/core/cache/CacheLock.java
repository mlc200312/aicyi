package io.github.aicyi.commons.core.cache;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 缓存锁接口
 * @date 2026/5/22
 **/
public interface CacheLock {

    /**
     * 尝试获取锁
     *
     * @param key 锁 key
     * @param ttl 锁租约时间，到期自动释放
     * @return 锁句柄（携带凭证，可在任意线程释放），获取失败返回 null
     */
    CacheLockHandle tryLock(String key, Duration ttl);
}