/**
 * 缓存抽象：Cache 契约、策略配置（CacheConfig）、加载器（CacheLoader）、防击穿锁（CacheLock/CacheLockHandle）。
 * <p>
 * 领域边界说明：
 * <ul>
 *   <li>{@link io.github.aicyi.commons.core.cache.CacheLock} 是缓存防护专用短租约锁：
 *       零等待 tryLock、允许获取失败，仅用于缓存回填防击穿；</li>
 *   <li>业务互斥请使用 {@link io.github.aicyi.commons.core.lock.DistributedLock}
 *       （可等待、面向临界区语义）。</li>
 * </ul>
 */
package io.github.aicyi.commons.core.cache;
