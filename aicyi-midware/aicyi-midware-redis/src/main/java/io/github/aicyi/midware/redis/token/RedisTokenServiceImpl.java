package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.token.TokenCreateRequest;
import io.github.aicyi.commons.security.token.exception.TokenExpiredException;
import io.github.aicyi.commons.security.token.exception.TokenInvalidException;
import io.github.aicyi.commons.util.Assert;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.*;
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
public class RedisTokenServiceImpl<P> implements RedisTokenService<P> {

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
     * JSON序列化
     */
    protected final JsonCodec jsonCodec;

    /**
     * Principal类型
     */
    private final Class<P> principalType;

    public RedisTokenServiceImpl(StringRedisTemplate redisTemplate, JsonCodec jsonCodec, Class<P> principalType) {
        this.redisTemplate = redisTemplate;
        this.jsonCodec = jsonCodec;
        this.principalType = principalType;
    }

    public RedisTokenServiceImpl(EnhancedRedisTemplateFactory factory, Class<P> principalType) {
        this(factory.getStringRedisTemplate(), new JacksonJsonCodec(factory.getObjectMapper()), principalType);
    }

    @Override
    public String create(TokenCreateRequest<P> request) {

        Assert.notNull(request, "tokenCreateRequest");

        Assert.notNull(request.getPrincipal(), "principal");

        try {

            String token = IdUtils.generateV7Id();

            long ttl = request.getTtl() > 0 ? request.getTimeUnit().toSeconds(request.getTtl()) : DEFAULT_TTL;

            RedisTokenInfo<P> tokenInfo = new RedisTokenInfo<>();

            tokenInfo.setToken(token);

            tokenInfo.setPrincipal(request.getPrincipal());

            tokenInfo.setAttributes(request.getAttributes());

            tokenInfo.setExpireAt(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttl));

            String tokenKey = buildTokenKey(token);

            String json = jsonCodec.toJson(tokenInfo);

            redisTemplate.opsForValue().set(tokenKey, json, Duration.ofSeconds(ttl));

            cachePrincipalToken(request.getPrincipal(), token, ttl);

            return token;

        } catch (Exception e) {

            throw new RuntimeException("create redis token failed", e);
        }
    }

    @Override
    public boolean isValid(String token) {

        try {

            RedisTokenInfo<P> tokenInfo = getTokenInfo(token);

            return tokenInfo != null;

        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public P parsePrincipal(String token) {

        RedisTokenInfo<P> tokenInfo = requireTokenInfo(token);

        return tokenInfo.getPrincipal();
    }

    @Override
    public Map<String, Object> parseAttributes(String token) {

        RedisTokenInfo<P> tokenInfo = requireTokenInfo(token);

        return Optional.ofNullable(tokenInfo.getAttributes()).orElse(Collections.emptyMap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getAttribute(String token, String attributeName) {

        RedisTokenInfo<P> tokenInfo = requireTokenInfo(token);

        if (tokenInfo.getAttributes() == null) {
            return null;
        }

        return (V) tokenInfo.getAttributes().get(attributeName);
    }

    @Override
    public String refresh(String token) {

        RedisTokenInfo<P> tokenInfo = requireTokenInfo(token);

        revoke(token);

        TokenCreateRequest<P> request = new TokenCreateRequest<>();

        request.setPrincipal(tokenInfo.getPrincipal());

        request.setAttributes(tokenInfo.getAttributes());

        request.setTtl(DEFAULT_REFRESH_TTL);

        request.setTimeUnit(TimeUnit.SECONDS);

        return create(request);
    }

    @Override
    public long getRemainingTtl(String token, TimeUnit unit) {

        Long seconds = redisTemplate.getExpire(buildTokenKey(token), TimeUnit.SECONDS);

        if (seconds == null || seconds <= 0) {

            throw new TokenExpiredException("token expired");
        }

        return unit.convert(seconds, TimeUnit.SECONDS);
    }

    @Override
    public Set<String> getTokens(P principal) {

        String principalKey = buildPrincipalTokensKey(principal);

        Set<String> tokens = redisTemplate.opsForSet().members(principalKey);

        return tokens == null ? Collections.emptySet() : tokens;
    }

    @Override
    public void revoke(String token) {

        RedisTokenInfo<P> tokenInfo = getTokenInfo(token);

        if (tokenInfo == null) {
            return;
        }

        redisTemplate.delete(buildTokenKey(token));

        redisTemplate.opsForSet().remove(buildPrincipalTokensKey(tokenInfo.getPrincipal()), token);
    }

    @Override
    public void revokeAll(P principal) {

        Set<String> tokens = getTokens(principal);

        for (String token : tokens) {

            redisTemplate.delete(buildTokenKey(token));
        }

        redisTemplate.delete(buildPrincipalTokensKey(principal));
    }

    /**
     * 获取Token信息
     */
    private RedisTokenInfo<P> getTokenInfo(String token) {

        try {

            String json = redisTemplate.opsForValue().get(buildTokenKey(token));

            if (json == null) {
                return null;
            }


            return jsonCodec.fromJson(json, JacksonTypeFactory.parametricTypeOf(RedisTokenInfo.class, JacksonTypeFactory.typeOf(principalType)));

        } catch (Exception e) {

            throw new TokenInvalidException("invalid token", e);
        }
    }

    /**
     * 获取Token信息（不存在则抛异常）
     */
    private RedisTokenInfo<P> requireTokenInfo(String token) {

        RedisTokenInfo<P> tokenInfo = getTokenInfo(token);

        if (tokenInfo == null) {

            throw new TokenExpiredException("token expired");
        }

        return tokenInfo;
    }

    /**
     * 缓存Principal Token
     */
    private void cachePrincipalToken(P principal, String token, long ttlSeconds) {

        String key = buildPrincipalTokensKey(principal);

        redisTemplate.opsForSet().add(key, token);

        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 构建Token Key
     */
    private String buildTokenKey(String token) {

        return TOKEN_KEY_PREFIX + token;
    }

    /**
     * 构建Principal Token集合Key
     */
    private String buildPrincipalTokensKey(P principal) {

        return PRINCIPAL_TOKENS_PREFIX + principal.hashCode();
    }
}