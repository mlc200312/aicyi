package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.CacheLock;
import io.github.aicyi.commons.core.cache.CacheLockHandle;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Mr.Min
 * @description Redis锁
 * @date 2026/5/22
 **/
public class RedisCacheLock implements CacheLock {

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setResultType(Long.class);
        UNLOCK_SCRIPT.setScriptText("if redis.call(\"get\", KEYS[1]) == ARGV[1]\n" +
                "            then\n" +
                "                return redis.call(\"del\", KEYS[1])\n" +
                "            else\n" +
                "                return 0\n" +
                "            end");
    }

    private final StringRedisTemplate template;

    public RedisCacheLock(StringRedisTemplate template) {
        this.template = template;
    }

    /**
     * 句柄闭包持有唯一凭证，无需 ThreadLocal，支持跨线程释放
     */
    @Override
    public CacheLockHandle tryLock(String key, Duration ttl) {
        String value = UUID.randomUUID().toString();

        Boolean success = template.opsForValue().setIfAbsent(key, value, ttl);

        if (!Boolean.TRUE.equals(success)) {
            return null;
        }

        return () -> template.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(key),
                value
        );
    }
}