package io.github.aicyi.midware.redis.cache;

import io.github.aicyi.commons.core.cache.CacheLock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

    /**
     * 按锁 key 持有各自的随机值，避免同一线程持有多个锁时互相覆盖
     */
    private final ThreadLocal<Map<String, String>> lockValues = ThreadLocal.withInitial(HashMap::new);

    public RedisCacheLock(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public boolean tryLock(String key, Duration ttl) {
        String value = UUID.randomUUID().toString();

        Boolean success = template.opsForValue().setIfAbsent(key, value, ttl);

        if (Boolean.TRUE.equals(success)) {
            lockValues.get().put(key, value);
            return true;
        }

        return false;
    }

    @Override
    public void unlock(String key) {
        Map<String, String> values = lockValues.get();

        String expected = values.remove(key);

        if (values.isEmpty()) {
            lockValues.remove();
        }

        if (expected == null) {
            return;
        }

        template.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(key),
                expected
        );
    }
}