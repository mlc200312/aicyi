package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.cache.CacheConfig;
import io.github.aicyi.commons.security.MessageDigestUtils;
import io.github.aicyi.commons.lang.model.TokenInfo;
import io.github.aicyi.commons.core.token.TokenSession;
import io.github.aicyi.commons.core.token.AbstractTokenService;
import io.github.aicyi.commons.lang.exception.TokenExpiredException;
import io.github.aicyi.commons.lang.exception.TokenInvalidException;
import io.github.aicyi.commons.util.UUIDUtils;
import io.github.aicyi.commons.util.serializer.CacheWrapperCodec;
import io.github.aicyi.midware.redis.cache.RedisCache;
import io.github.aicyi.midware.redis.cache.RedisCacheConfig;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis Token Service 实现
 *
 * <p>
 * Token数据完全存储于Redis：
 * </p>
 *
 * <ul>
 *     <li>支持Token主动失效</li>
 *     <li>支持多端登录</li>
 *     <li>支持踢人下线</li>
 *     <li>支持Token续期</li>
 *     <li>支持分布式部署</li>
 * </ul>
 *
 * @param <P> Principal类型
 * @author Mr.Min
 */
public class RedisTokenServiceImpl<P> extends AbstractTokenService<P> implements RedisTokenService<P> {

    /**
     * Token Key前缀
     */
    private static final String TOKEN_KEY_PREFIX = "security:token:";

    /**
     * Principal Token集合Key前缀
     */
    private static final String PRINCIPAL_TOKENS_PREFIX = "security:principal:tokens:";

    /**
     * token 缓存
     */
    protected final RedisCache<TokenSession<P>> tokenCache;

    /**
     * redis 操作
     */
    protected final StringRedisTemplate redisTemplate;

    public RedisTokenServiceImpl(RedisCache<TokenSession<P>> tokenCache, long refreshTtl, TimeUnit refreshTimeUnit) {
        super(refreshTtl, refreshTimeUnit);
        this.tokenCache = tokenCache;
        this.redisTemplate = tokenCache.getTemplate();
    }

    public RedisTokenServiceImpl(StringRedisTemplate redisTemplate, Class<? extends P> principalType, long refreshTtl, TimeUnit refreshTimeUnit) {
        super(refreshTtl, refreshTimeUnit);
        CacheConfig cacheConfig = RedisCacheConfig.builder()
                .globalPrefix("cache")
                .cacheName("token")
                .ttl(Duration.ofMillis(refreshTimeUnit.toMillis(refreshTtl)))
                .build();
        this.tokenCache = new RedisCache<>(
                redisTemplate,
                cacheConfig,
                new CacheWrapperCodec<>(TokenInfo.class, principalType)
        );
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected String createToken() {
        return UUIDUtils.generateV7Id();
    }

    @Override
    protected String getTokenId(String token) {
        return TOKEN_KEY_PREFIX + token;
    }

    /**
     * Principal Token集合Key
     * <p>
     * 默认基于 Principal 内容的 SHA-256 摘要，避免 hashCode 碰撞导致不同用户共享 Token 集合；
     * 子类应优先覆写为稳定的业务 ID（如 userId）
     */
    @Override
    protected String getPrincipalId(P principal) {
        return PRINCIPAL_TOKENS_PREFIX + MessageDigestUtils.generateSha256(String.valueOf(principal));
    }

    @Override
    protected void saveTokenSession(TokenSession<P> session, long ttlSeconds) {

        String tokenId = getTokenId(session.getToken());

        tokenCache.put(tokenId, session, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    protected void cachePrincipalToken(P principal, String token, long ttlSeconds) {

        String principalId = getPrincipalId(principal);

        redisTemplate.opsForSet().add(principalId, token);

        redisTemplate.expire(principalId, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    protected TokenSession<P> getTokenSession(String token) throws TokenInvalidException {

        try {

            String tokenId = getTokenId(token);

            TokenSession<P> session = tokenCache.get(tokenId);

            if (session == null) {
                return null;
            }

            return session;

        } catch (Exception e) {

            throw new TokenInvalidException("invalid token", e);
        }
    }

    @Override
    public long getRemainingTtl(String token, TimeUnit unit) {

        String tokenId = getTokenId(token);

        Long seconds = tokenCache.getExpire(tokenId, TimeUnit.SECONDS);

        if (seconds == null || seconds <= 0) {

            throw new TokenExpiredException("token expired");
        }

        return unit.convert(seconds, TimeUnit.SECONDS);
    }

    @Override
    public Set<String> getTokens(P principal) {

        String principalId = getPrincipalId(principal);

        Set<String> tokens = redisTemplate.opsForSet().members(principalId);

        return tokens == null ? Collections.emptySet() : tokens;
    }

    @Override
    public void revoke(String token) {

        TokenSession<P> session = getTokenSession(token);

        if (session == null) {
            return;
        }

        String tokenId = getTokenId(token);

        P principal = session.getPrincipal();

        tokenCache.evict(tokenId);

        revokePrincipal(principal, token);
    }

    @Override
    public void revokeAll(P principal) {

        Set<String> tokens = getTokens(principal);

        for (String token : tokens) {

            String tokenId = getTokenId(token);

            tokenCache.evict(tokenId);
        }

        revokePrincipalAll(principal);
    }

    protected void revokePrincipal(P principal, String token) {

        String principalId = getPrincipalId(principal);

        redisTemplate.opsForSet().remove(principalId, token);
    }

    protected void revokePrincipalAll(P principal) {

        String principalId = getPrincipalId(principal);

        redisTemplate.delete(principalId);
    }
}