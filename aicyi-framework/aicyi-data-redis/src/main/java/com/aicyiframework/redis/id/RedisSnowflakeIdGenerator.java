package com.aicyiframework.redis.id;

import com.aichuangyi.commons.util.id.Snowflake;
import com.aicyiframework.core.IdGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisSnowflakeIdGenerator extends Snowflake implements IdGenerator {

    // Lua脚本用于原子性获取和重置workerId
    private static final String WORKER_ID_LUA_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local max_id = tonumber(ARGV[1])\n" +
                    "local current = tonumber(redis.call('GET', key) or 0)\n" +
                    "if current < max_id then\n" +
                    "    redis.call('INCR', key)\n" +
                    "    return tonumber(redis.call('GET', key)) - 1\n" +
                    "else\n" +
                    "    redis.call('SET', key, 0)\n" +
                    "    return 0\n" +
                    "end";

    private final Lock lock = new ReentrantLock();
    private final RedisTemplate<String, String> redisTemplate;

    public RedisSnowflakeIdGenerator(RedisTemplate<String, String> redisTemplate) {
        super(0, 0);
        this.redisTemplate = redisTemplate;
        this.setWorkerId(getRedisWorkerId());
    }

    @Override
    public synchronized long nextId() {
        lock.lock();
        try {
            return super.nextId();
        } finally {
            lock.unlock();
        }
    }

    private long getRedisWorkerId() {
        String key = "snowflake:worker_id";
        RedisScript<Long> script = new DefaultRedisScript<>(WORKER_ID_LUA_SCRIPT, Long.class);

        // 设置key的过期时间，防止长期占用
        redisTemplate.expire(key, 1, TimeUnit.DAYS);

        Long workerId = redisTemplate.execute(script, Collections.singletonList(key), String.valueOf(this.getWorkerId()));

        if (workerId == null) {
            throw new RuntimeException("Failed to allocate worker ID from Redis");
        }
        return workerId;
    }
}
