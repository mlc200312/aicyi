package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.token.TokenCreateRequest;
import io.github.aicyi.commons.security.token.TokenSessionPrincipalSerializer;
import io.github.aicyi.commons.security.token.exception.TokenExpiredException;
import io.github.aicyi.commons.util.Assert;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Redis多Token管理实现
 * @date 17:07
 **/
public class MultiRedisTokenServiceImpl<P extends IJWTInfo> extends RedisTokenServiceImpl<P> implements RedisTokenService<P> {

    /**
     * 用户Token集合前缀
     */
    private static final String USER_TOKENS_KEY_PREFIX = "security:user:tokens:";

    /**
     * 是否允许多设备登录
     */
    private boolean isMultiTokenAllowed = false;

    /**
     * 多设备登录数量
     */
    private int multiTokenCount = 1;

    public MultiRedisTokenServiceImpl(StringRedisTemplate redisTemplate, Class<? extends P> principalType, long refreshTtl, TimeUnit refreshTimeUnit, boolean isMultiTokenAllowed, int multiTokenCount) {
        super(redisTemplate, new TokenSessionPrincipalSerializer<>(principalType), refreshTtl, refreshTimeUnit);
        this.isMultiTokenAllowed = isMultiTokenAllowed;
        this.multiTokenCount = multiTokenCount;
    }

    public MultiRedisTokenServiceImpl(StringRedisTemplate redisTemplate, Class<? extends P> principalType, long refreshTtl, TimeUnit refreshTimeUnit) {
        super(redisTemplate, new TokenSessionPrincipalSerializer<>(principalType), refreshTtl, refreshTimeUnit);
    }

    public boolean isMultiTokenAllowed() {
        return isMultiTokenAllowed;
    }

    public void setMultiTokenAllowed(boolean multiTokenAllowed) {
        isMultiTokenAllowed = multiTokenAllowed;
    }

    public int getMultiTokenCount() {
        return multiTokenCount;
    }

    public void setMultiTokenCount(int multiTokenCount) {
        this.multiTokenCount = multiTokenCount;
    }

    @Override
    protected String getPrincipalId(P principal) {
        return USER_TOKENS_KEY_PREFIX + principal.getId();
    }

    @Override
    protected void cachePrincipalToken(P principal, String token, long ttlSeconds) {

        String principalId = getPrincipalId(principal);

        long now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(principalId, token, now);

        redisTemplate.expire(principalId, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public String create(TokenCreateRequest<P> request) {

        Assert.notNull(request, "tokenCreateRequest");

        P principal = request.getPrincipal();

        Assert.notNull(principal, "principal");

        String principalId = getPrincipalId(principal);

        removeExpiredTokens(principalId);

        if (isMultiTokenAllowed) {

            Long currentDeviceCount = redisTemplate.opsForZSet().zCard(principalId);

            if (currentDeviceCount != null && currentDeviceCount >= multiTokenCount) {

                kickOutOldestToken(principalId);
            }

        } else {

            revokeAll(principal);
        }

        return super.create(request);
    }

    @Override
    public Set<String> getTokens(P principal) {

        String principalId = getPrincipalId(principal);

        Set<String> tokens = redisTemplate.opsForZSet().range(principalId, 0, -1);

        return tokens == null ? Collections.emptySet() : tokens;
    }

    @Override
    public void revokeAll(P principal) {

        String principalId = getPrincipalId(principal);

        Set<String> tokens = redisTemplate.opsForZSet().range(principalId, 0, -1);

        if (tokens != null) {

            for (String token : tokens) {

                super.revoke(token);
            }
        }

        redisTemplate.delete(principalId);
    }

    @Override
    protected void revokePrincipal(P principal, String token) {

        String principalId = getPrincipalId(principal);

        redisTemplate.opsForZSet().remove(principalId, token);
    }

    /**
     * 剔除老设备
     *
     * @param principalId
     */
    private void kickOutOldestToken(String principalId) {

        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeWithScores(principalId, 0, 0);

        if (tuples == null || tuples.isEmpty()) {

            return;
        }

        ZSetOperations.TypedTuple<String> tuple = tuples.iterator().next();

        String oldestToken = tuple.getValue();

        if (oldestToken == null) {
            return;
        }

        super.revoke(oldestToken);

        redisTemplate.opsForZSet().remove(principalId, oldestToken);

        logger.info("剔除设备：{}", oldestToken);
    }

    /**
     * 删除已过期Token
     *
     * @param principalId
     */
    private void removeExpiredTokens(String principalId) {

        Set<String> tokens = redisTemplate.opsForZSet().range(principalId, 0, -1);

        if (tokens == null || tokens.isEmpty()) {

            return;
        }

        for (String token : tokens) {

            try {

                isValid(token);

            } catch (TokenExpiredException e) {

                redisTemplate.opsForZSet().remove(principalId, token);
            }
        }
    }
}
