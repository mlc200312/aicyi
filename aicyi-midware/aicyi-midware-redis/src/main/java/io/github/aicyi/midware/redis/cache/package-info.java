/**
 * Redis 缓存实现：{@link io.github.aicyi.midware.redis.cache.RedisCache} 及其配置、防击穿锁实现。
 * <p>
 * 部署形态限制：批量写脚本（多 KEYS）与 {@code clear()} 的批量 DEL 要求全部 key
 * 位于同一 slot，跨 slot 的 Redis Cluster 模式下需为 cacheName 配置 hash tag
 * （如 "{user}"）或改用单机/哨兵部署。
 */
package io.github.aicyi.midware.redis.cache;
