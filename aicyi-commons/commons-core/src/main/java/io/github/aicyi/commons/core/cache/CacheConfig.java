package io.github.aicyi.commons.core.cache;

import io.github.aicyi.commons.core.PrincipalSerializer;

import java.time.Duration;

/**
 * @author Mr.Min
 * @description 缓存配置接口
 * @date 11:10
 **/
public interface CacheConfig<P> {

    String getGlobalPrefix();

    String getCacheName();

    Duration getTtl();

    boolean isCacheNull();

    boolean isTtlJitter();

    int getJitterPercent();

    Duration getLockTtl();

    Duration getWaitTimeout();

    PrincipalSerializer<P> getSerializer();
}
