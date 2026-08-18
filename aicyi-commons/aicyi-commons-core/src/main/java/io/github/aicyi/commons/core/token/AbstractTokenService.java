package io.github.aicyi.commons.core.token;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.lang.exception.TokenException;
import io.github.aicyi.commons.lang.exception.TokenExpiredException;
import io.github.aicyi.commons.lang.exception.TokenInvalidException;
import io.github.aicyi.commons.lang.model.TokenCreateRequest;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.commons.lang.model.TokenInfo;
import io.github.aicyi.commons.logging.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 抽象TokenService
 * @date 15:17
 **/
public abstract class AbstractTokenService<P> implements TokenService<String, P> {

    protected static final long DEFAULT_TTL = TimeUnit.DAYS.toSeconds(7);

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final long refreshTtl;

    private final TimeUnit refreshTimeUnit;

    public AbstractTokenService(long refreshTtl, TimeUnit refreshTimeUnit) {
        this.refreshTtl = refreshTtl;
        this.refreshTimeUnit = refreshTimeUnit;
    }

    public long getRefreshTtl() {
        return refreshTtl;
    }

    public TimeUnit getRefreshTimeUnit() {
        return refreshTimeUnit;
    }

    @Override
    public String create(TokenCreateRequest<P> request) {

        Assert.notNull(request, "tokenCreateRequest");

        Assert.notNull(request.getPrincipal(), "principal");

        // 1.创建Token
        String token = createToken();

        // 2.保存TokenSession
        long ttl = getDefaultTtl(request);

        TokenSession<P> session = newTokenSession(request, token, ttl);

        saveTokenSession(session, ttl);

        // 3.缓存PrincipalToken
        cachePrincipalToken(session.getPrincipal(), session.getToken(), ttl);

        // 4.返回Token
        return token;
    }

    @Override
    public boolean isValid(String token) {

        try {

            TokenSession<P> session = getTokenSession(token);

            return session != null;

        } catch (TokenException e) {

            return false;
        }
    }

    @Override
    public P parsePrincipal(String token) {

        TokenSession<P> session = requireTokenSession(token);

        return session.getPrincipal();
    }

    @Override
    public Map<String, Object> parseAttributes(String token) {

        TokenSession<P> tokenSession = requireTokenSession(token);

        return Optional.ofNullable(tokenSession.getAttributes()).orElse(Collections.emptyMap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getAttribute(String token, String attributeName) {

        TokenSession<P> session = requireTokenSession(token);

        if (session.getAttributes() == null) {
            return null;
        }

        return (V) session.getAttributes().get(attributeName);
    }

    @Override
    public String refresh(String token) {

        // 1.获取Token信息
        TokenSession<P> session = requireTokenSession(token);

        // 2.构造TokenCreateRequest
        TokenCreateRequest<P> request = new TokenCreateRequest<>();

        request.setPrincipal(session.getPrincipal());

        request.setAttributes(session.getAttributes());

        request.setTtl(getRefreshTtl());

        request.setTimeUnit(getRefreshTimeUnit());

        // 3.先创建新Token：创建失败时旧Token仍可用，避免用户被意外踢出
        String newToken = create(request);

        // 4.新Token生效后再吊销旧Token
        revoke(token);

        return newToken;
    }

    protected abstract String createToken();

    protected abstract String getTokenId(String token);

    protected abstract String getPrincipalId(P principal);

    protected abstract void saveTokenSession(TokenSession<P> session, long ttlSeconds);

    protected abstract void cachePrincipalToken(P principal, String token, long ttlSeconds);

    protected abstract TokenSession<P> getTokenSession(String token) throws TokenInvalidException;

    /**
     * 创建TokenSession
     *
     * @param request
     * @return
     */
    protected TokenSession<P> newTokenSession(TokenCreateRequest<P> request, String token, long ttlSeconds) {

        TokenInfo<P> tokenInfo = new TokenInfo<>();

        tokenInfo.setToken(token);

        tokenInfo.setPrincipal(request.getPrincipal());

        tokenInfo.setAttributes(request.getAttributes());

        tokenInfo.setIssuedAt(System.currentTimeMillis());

        tokenInfo.setExpireAt(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttlSeconds));

        return tokenInfo;
    }

    /**
     * 获取默认TTL（秒）：request 未指定有效时间或时间单位时回退默认值
     */
    protected long getDefaultTtl(TokenCreateRequest<P> request) {

        if (request.getTtl() <= 0) {

            return DEFAULT_TTL;
        }

        TimeUnit timeUnit = request.getTimeUnit() == null ? TimeUnit.SECONDS : request.getTimeUnit();

        return timeUnit.toSeconds(request.getTtl());
    }

    /**
     * 获取Token信息（不存在则抛异常）
     */
    private TokenSession<P> requireTokenSession(String token) {

        TokenSession<P> session = getTokenSession(token);

        if (session == null) {

            throw new TokenExpiredException("token expired");
        }

        return session;
    }
}
