package io.github.aicyi.commons.lang.model;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Mr.Min
 * @description 缓存统计类
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

    public LongAdder getHitCount() {
        return hitCount;
    }

    public LongAdder getMissCount() {
        return missCount;
    }

    public LongAdder getLoadSuccessCount() {
        return loadSuccessCount;
    }

    public LongAdder getLoadFailureCount() {
        return loadFailureCount;
    }

    public LongAdder getPutCount() {
        return putCount;
    }

    public LongAdder getEvictCount() {
        return evictCount;
    }

    public LongAdder getTotalLoadTimeNanos() {
        return totalLoadTimeNanos;
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
}