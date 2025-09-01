package com.aichuangyi.commons.core.cache;

/**
 * @author Mr.Min
 * @description 缓存统计接口
 * @date 2025/8/12
 **/
public interface CacheStats {

    /**
     * 获取缓存命中次数
     */
    long getHitCount();

    /**
     * 获取缓存未命中次数
     */
    long getMissCount();

    /**
     * 获取缓存加载成功次数
     */
    long getLoadSuccessCount();

    /**
     * 获取缓存加载失败次数
     */
    long getLoadFailureCount();

    /**
     * 获取缓存命中率
     */
    default double getHitRate() {
        long requestCount = getHitCount() + getMissCount();
        return requestCount == 0 ? 1.0 : (double) getHitCount() / requestCount;
    }

    /**
     * 获取缓存加载失败率
     */
    default double getLoadFailureRate() {
        long totalLoads = getLoadSuccessCount() + getLoadFailureCount();
        return totalLoads == 0 ? 0.0 : (double) getLoadFailureCount() / totalLoads;
    }
}