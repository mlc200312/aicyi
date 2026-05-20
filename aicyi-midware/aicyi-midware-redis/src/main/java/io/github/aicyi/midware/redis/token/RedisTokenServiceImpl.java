package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.token.PrincipalSerializer;
import io.github.aicyi.commons.security.token.TokenSession;
import io.github.aicyi.commons.security.token.AbstractTokenService;
import io.github.aicyi.commons.security.token.TokenSessionPrincipalSerializer;
import io.github.aicyi.commons.security.token.exception.TokenExpiredException;
import io.github.aicyi.commons.security.token.exception.TokenInvalidException;
import io.github.aicyi.commons.util.id.IdUtils;
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
     * RedisTemplate
     */
    protected final StringRedisTemplate redisTemplate;

    /**
     * Principal序列化器
     */
    protected final PrincipalSerializer<TokenSession<P>> serializer;

    /**
     * 默认refresh ttl
     */
    private long refreshTtl;

    /**
     * 默认refresh ttl 单位
     */
    private TimeUnit refreshTimeUnit;

    public RedisTokenServiceImpl(StringRedisTemplate redisTemplate, PrincipalSerializer<TokenSession<P>> serializer, long refreshTtl, TimeUnit refreshTimeUnit) {
        super(refreshTtl, refreshTimeUnit);
        this.redisTemplate = redisTemplate;
        this.serializer = serializer;
        this.refreshTtl = refreshTtl;
        this.refreshTimeUnit = refreshTimeUnit;
    }

    public RedisTokenServiceImpl(StringRedisTemplate redisTemplate, Class<P> principalType, long refreshTtl, TimeUnit refreshTimeUnit) {
        this(redisTemplate, new TokenSessionPrincipalSerializer<>(principalType), refreshTtl, refreshTimeUnit);
    }

    @Override
    protected String createToken() {
        return IdUtils.generateV7Id();
    }

    @Override
    protected String getTokenId(String token) {
        return TOKEN_KEY_PREFIX + token;
    }

    @Override
    protected String getPrincipalId(P principal) {
        return PRINCIPAL_TOKENS_PREFIX + principal.hashCode();
    }

    @Override
    protected void saveTokenSession(TokenSession<P> session, long ttlSeconds) {

        String json = serializer.serialize(session);

        String tokenId = getTokenId(session.getToken());

        redisTemplate.opsForValue().set(tokenId, json, Duration.ofSeconds(ttlSeconds));
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

            String json = redisTemplate.opsForValue().get(tokenId);

            if (json == null) {
                return null;
            }

            return serializer.deserialize(json);

        } catch (Exception e) {

            throw new TokenInvalidException("invalid token", e);
        }
    }

    @Override
    public long getRemainingTtl(String token, TimeUnit unit) {

        String tokenId = getTokenId(token);

        Long seconds = redisTemplate.getExpire(tokenId, TimeUnit.SECONDS);

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

        redisTemplate.delete(tokenId);

        revokePrincipal(principal, token);
    }

    @Override
    public void revokeAll(P principal) {

        Set<String> tokens = getTokens(principal);

        for (String token : tokens) {

            String tokenId = getTokenId(token);

            redisTemplate.delete(tokenId);
        }

        String principalId = getPrincipalId(principal);

        redisTemplate.delete(principalId);
    }

    protected void revokePrincipal(P principal, String token) {

        String principalId = getPrincipalId(principal);

        redisTemplate.opsForSet().remove(principalId, token);
    }
}