package io.github.aicyi.commons.core.cache;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 缓存配置接口（面向分布式缓存的「身份 + 策略」契约：
 * globalPrefix/cacheName 为键空间身份，其余为通用策略属性；不含存储/序列化细节）
 * @date 11:10
 **/
public interface CacheConfig {

    String getGlobalPrefix();

    String getCacheName();

    Duration getTtl();

    boolean isCacheNull();

    boolean isTtlJitter();

    int getJitterPercent();

    Duration getLockTtl();

    Duration getWaitTimeout();
}
