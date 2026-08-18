package io.github.aicyi.commons.core.cache;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Mr.Min
 * @description 缓存统计类（LongAdder 不实现 Serializable，故不继承 BaseBean）。
 * 外部读取请使用 long 求值 getter（hitCount() 等），避免通过 LongAdder getter 篡改计数
 * @date 2026/5/22
 **/
public class CacheStats {
    private final LongAdder hitCount = new LongAdder();
    private final LongAdder missCount = new LongAdder();
    private final LongAdder loadSuccessCount = new LongAdder();
    private final LongAdder loadFailureCount = new LongAdder();
    private final LongAdder putCount = new LongAdder();
    private final LongAdder evictCount = new LongAdder();

    private final LongAdder totalLoadTimeNanos = new LongAdder();

    public long hitCount() {
        return hitCount.sum();
    }

    public long missCount() {
        return missCount.sum();
    }

    public long loadSuccessCount() {
        return loadSuccessCount.sum();
    }

    public long loadFailureCount() {
        return loadFailureCount.sum();
    }

    public long putCount() {
        return putCount.sum();
    }

    public long evictCount() {
        return evictCount.sum();
    }

    public long totalLoadTimeNanos() {
        return totalLoadTimeNanos.sum();
    }

    public void recordHit() {
        hitCount.increment();
    }

    public void recordMiss() {
        missCount.increment();
    }

    public void recordLoadSuccess(long nanos) {
        loadSuccessCount.increment();
        totalLoadTimeNanos.add(nanos);
    }

    public void recordLoadFailure(long nanos) {
        loadFailureCount.increment();
        totalLoadTimeNanos.add(nanos);
    }

    public void recordPut() {
        putCount.increment();
    }

    public void recordEvict() {
        evictCount.increment();
    }

    public double hitRate() {
        long hit = hitCount.sum();
        long miss = missCount.sum();

        long total = hit + miss;

        return total == 0 ? 0 : (double) hit / total;
    }

    public long avgLoadTimeMillis() {
        long count = loadSuccessCount.sum() + loadFailureCount.sum();

        return count == 0 ? 0 :
                totalLoadTimeNanos.sum() / count / 1_000_000;
    }

    /**
     * 生成当前计数的不可变快照，外部对快照的修改不影响源统计
     */
    public CacheStats snapshot() {
        CacheStats copy = new CacheStats();
        copy.hitCount.add(hitCount.sum());
        copy.missCount.add(missCount.sum());
        copy.loadSuccessCount.add(loadSuccessCount.sum());
        copy.loadFailureCount.add(loadFailureCount.sum());
        copy.putCount.add(putCount.sum());
        copy.evictCount.add(evictCount.sum());
        copy.totalLoadTimeNanos.add(totalLoadTimeNanos.sum());
        return copy;
    }
}