package io.github.aicyi.midware.redis.id;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.UUIDUtils;
import io.github.aicyi.commons.core.id.WorkerIdAllocator;
import io.github.aicyi.commons.core.id.WorkerIdLease;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.Collections;

/**
 * @author Mr.Min
 * @description Redis 分布式 WorkerId 分配器
 * @date 2026/5/21
 **/
public class RedisWorkerIdAllocator implements WorkerIdAllocator {

    /**
     * token 一致才续约（静态单例，心跳高频调用避免重复构造）
     */
    private static final DefaultRedisScript<String> RENEW_SCRIPT;

    /**
     * token 一致才删除
     */
    private static final DefaultRedisScript<String> RELEASE_SCRIPT;

    static {
        RENEW_SCRIPT = new DefaultRedisScript<>();
        RENEW_SCRIPT.setResultType(String.class);
        RENEW_SCRIPT.setScriptText(
                "local v = redis.call('GET', KEYS[1]) " +
                        "if v == ARGV[1] then " +
                        "   redis.call('EXPIRE', KEYS[1], ARGV[2]) " +
                        "   return '1' " +
                        "else " +
                        "   return '0' " +
                        "end"
        );

        RELEASE_SCRIPT = new DefaultRedisScript<>();
        RELEASE_SCRIPT.setResultType(String.class);
        RELEASE_SCRIPT.setScriptText(
                "local v = redis.call('GET', KEYS[1]) " +
                        "if v == ARGV[1] then " +
                        "   redis.call('DEL', KEYS[1]) " +
                        "   return '1' " +
                        "else " +
                        "   return '0' " +
                        "end"
        );
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * String Redis 模板
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * workerId 最大值，比如 31 / 63 / 1023
     */
    private final int maxWorkerId;

    /**
     * 服务名
     */
    private final String serviceName;

    /**
     * TTL
     */
    private final long ttlSeconds;

    public RedisWorkerIdAllocator(StringRedisTemplate redisTemplate, String serviceName, int maxWorkerId, long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.serviceName = serviceName;
        this.maxWorkerId = maxWorkerId;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public WorkerIdLease allocate() {
        String token = UUIDUtils.generateV7Id();

        for (int workerId = 0; workerId <= maxWorkerId; workerId++) {
            String key = buildKey(workerId);

            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, token, Duration.ofSeconds(ttlSeconds));

            if (Boolean.TRUE.equals(success)) {
                logger.info("Allocated workerId={}, token={}", workerId, token);
                return new WorkerIdLease(workerId, token, ttlSeconds);
            }
        }

        throw new IllegalStateException("No available workerId. maxWorkerId=" + maxWorkerId);
    }

    @Override
    public boolean renew(WorkerIdLease lease) {
        String result = redisTemplate.execute(
                RENEW_SCRIPT,
                Collections.singletonList(buildKey(lease.getWorkerId())),
                lease.getToken(),
                String.valueOf(ttlSeconds)
        );

        boolean success = "1".equals(result);

        if (success) {
            logger.debug("Renew success workerId={}", lease.getWorkerId());
        } else {
            logger.warn("Renew failed workerId={}", lease.getWorkerId());
        }

        return success;
    }

    @Override
    public boolean release(WorkerIdLease lease) {
        String result = redisTemplate.execute(RELEASE_SCRIPT, Collections.singletonList(buildKey(lease.getWorkerId())), lease.getToken());

        boolean success = "1".equals(result);

        if (success) {
            logger.info("Released workerId={}", lease.getWorkerId());
        } else {
            logger.warn("Release ignored workerId={}", lease.getWorkerId());
        }

        return success;
    }

    private String buildKey(int workerId) {
        return "worker:id:" + serviceName + ":" + workerId;
    }
}