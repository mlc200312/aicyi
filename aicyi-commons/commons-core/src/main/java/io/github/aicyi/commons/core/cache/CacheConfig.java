package io.github.aicyi.commons.core.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 缓存配置接口
 * @date 11:10
 **/
public interface CacheConfig {

    long getDefaultExpireTime();

    TimeUnit getDefaultTimeUnit();

    int getMaxSize();

    boolean isRecordStats();
}
