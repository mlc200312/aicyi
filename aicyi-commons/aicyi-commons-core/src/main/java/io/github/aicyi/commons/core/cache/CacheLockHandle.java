package io.github.aicyi.commons.core.cache;

/**
 * @author Mr.Min
 * @description 缓存锁句柄：持有锁凭证，支持跨线程释放与 try-with-resources
 * @date 2026/8/14
 **/
public interface CacheLockHandle extends AutoCloseable {

    /**
     * 释放锁（仅持有者可释放，实现应校验凭证）
     */
    void unlock();

    @Override
    default void close() {
        unlock();
    }
}
