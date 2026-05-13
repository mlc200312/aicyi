package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.token.TokenCreateRequest;
import io.github.aicyi.commons.security.token.exception.TokenExpiredException;
import io.github.aicyi.commons.util.Assert;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mr.Min
 * @description Redis多Token管理实现
 * @date 17:07
 **/
public class MultiRedisTokenServiceImpl<P extends IJWTInfo> extends RedisTokenServiceImpl<P> {

    /**
     * 用户Token集合前缀
     */
    private static final String USER_TOKENS_KEY_PREFIX = "security:user:tokens:";

    private boolean isMultiTokenAllowed = false;
    private int multiTokenCount = 1;

    public MultiRedisTokenServiceImpl(StringRedisTemplate redisTemplate, JsonCodec jsonCodec, Class<P> principalType) {
        super(redisTemplate, jsonCodec, principalType);
    }

    public MultiRedisTokenServiceImpl(EnhancedRedisTemplateFactory factory, Class<P> principalType) {
        super(factory, principalType);
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
    public String create(TokenCreateRequest<P> request) {

        Assert.notNull(request, "tokenCreateRequest");

        Assert.notNull(request.getPrincipal(), "principal");

        P principal = request.getPrincipal();

        String userTokensKey = buildUserTokensKey(principal.getId());

        removeExpiredTokens(userTokensKey);

        if (isMultiTokenAllowed) {

            Long currentDeviceCount = redisTemplate.opsForZSet().zCard(userTokensKey);

            if (currentDeviceCount != null && currentDeviceCount >= multiTokenCount) {

                kickOutOldestToken(userTokensKey);
            }

        } else {

            revokeAll(principal);
        }

        String token = super.create(request);

        long now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(userTokensKey, token, now);

        long ttl = request.getTtl() > 0 ? request.getTimeUnit().toSeconds(request.getTtl()) : DEFAULT_TTL;

        redisTemplate.expire(userTokensKey, Duration.ofSeconds(ttl));

        return token;
    }

    @Override
    public Set<String> getTokens(P principal) {
        String userTokensKey = buildUserTokensKey(principal.getId());

        Set<String> tokens = redisTemplate.opsForZSet().range(userTokensKey, 0, -1);

        return tokens == null ? Collections.emptySet() : tokens;
    }

    @Override
    public void revoke(String token) {
        P principal;

        try {

            principal = parsePrincipal(token);

        } catch (Exception e) {

            super.revoke(token);

            return;
        }

        super.revoke(token);

        redisTemplate.opsForZSet().remove(buildUserTokensKey(principal.getId()), token);
    }

    @Override
    public void revokeAll(P principal) {

        String userTokensKey = buildUserTokensKey(principal.getId());

        Set<String> tokens = redisTemplate.opsForZSet().range(userTokensKey, 0, -1);

        if (tokens != null) {

            for (String token : tokens) {

                super.revoke(token);
            }
        }

        redisTemplate.delete(userTokensKey);
    }


    /**
     * 剔除老设备
     *
     * @param userTokenKey
     */
    private void kickOutOldestToken(String userTokenKey) {

        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeWithScores(userTokenKey, 0, 0);

        if (tuples == null || tuples.isEmpty()) {

            return;
        }

        ZSetOperations.TypedTuple<String> tuple = tuples.iterator().next();

        String oldestToken = tuple.getValue();

        if (oldestToken == null) {
            return;
        }

        super.revoke(oldestToken);

        redisTemplate.opsForZSet().remove(userTokenKey, oldestToken);
    }

    /**
     * 删除已过期Token
     *
     * @param userTokenKey
     */
    private void removeExpiredTokens(String userTokenKey) {

        Set<String> tokens = redisTemplate.opsForZSet().range(userTokenKey, 0, -1);

        if (tokens == null || tokens.isEmpty()) {

            return;
        }

        for (String token : tokens) {

            try {

                isValid(token);

            } catch (TokenExpiredException e) {

                redisTemplate.opsForZSet().remove(userTokenKey, token);
            }
        }
    }

    /**
     * 构建用户Token集合Key
     */
    private String buildUserTokensKey(Object userId) {

        return USER_TOKENS_KEY_PREFIX + userId;
    }
}
