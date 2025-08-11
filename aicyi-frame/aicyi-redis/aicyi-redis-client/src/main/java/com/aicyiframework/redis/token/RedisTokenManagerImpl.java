package com.aicyiframework.redis.token;

import com.aichuangyi.core.AbstractTokenManager;
import com.aichuangyi.core.TokenManager;
import com.aichuangyi.commons.util.Assert;
import com.aicyiframework.redis.EnhancedRedisTemplateFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description redis + jjwt 实现分布式token
 * @date 18:37
 **/
public class RedisTokenManagerImpl extends AbstractTokenManager implements TokenManager {
    private static final String TOKEN_KEY_PREFIX = "redis:token:";

    private RedisTemplate<String, String> stringRedisTemplate;
    private TokenManager tokenManager;

    private RedisTokenManagerImpl() {
    }

    public RedisTokenManagerImpl(RedisConnectionFactory redisConnectionFactory, TokenManager tokenManager) {
        Assert.notNull(redisConnectionFactory, "redisConnectionFactory");
        Assert.notNull(tokenManager, "tokenManager");
        this.stringRedisTemplate = new EnhancedRedisTemplateFactory(redisConnectionFactory).getStringTemplate();
        this.tokenManager = tokenManager;
    }

    @Override
    public String createToken(String id, Date expireAt, Map<String, Object> claimMap) {
        String token = tokenManager.createToken(id, null, claimMap);
        long timeout = expireAt.getTime() - System.currentTimeMillis() / 1000;
        Assert.positive(timeout, "timeout");
        String redisKey = getTokenKey(id);
        stringRedisTemplate.boundValueOps(redisKey).set(token, timeout, TimeUnit.SECONDS);
        stringRedisTemplate.expireAt(redisKey, expireAt);
        return token;
    }

    @Override
    public String refreshToken(String token, Date expireAt) {
        String id = tokenManager.getId(token);
        String redisKey = getTokenKey(id);
        stringRedisTemplate.expireAt(redisKey, expireAt);
        return token;
    }

    @Override
    public boolean verifyToken(String token) {
        if (tokenManager.verifyToken(token)) {
            String id = tokenManager.getId(token);
            String redisKey = getTokenKey(id);
            Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            return expire != null && expire > 0;
        }
        return false;
    }

    @Override
    public String getId(String token) {
        return tokenManager.getId(token);
    }

    @Override
    public Date getExpire(String token) {
        String id = tokenManager.getId(token);
        String redisKey = getTokenKey(id);
        Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
        return new Date(System.currentTimeMillis() + expire);
    }

    @Override
    public <T> T get(String token, String key, Class<T> clazz) {
        return tokenManager.get(token, key, clazz);
    }

    protected String getTokenKey(String id) {
        return TOKEN_KEY_PREFIX + id;
    }
}
