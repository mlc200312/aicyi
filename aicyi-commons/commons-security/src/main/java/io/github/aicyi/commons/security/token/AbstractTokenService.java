package io.github.aicyi.commons.security.token;

import io.github.aicyi.commons.core.token.TokenCreateRequest;
import io.github.aicyi.commons.core.token.TokenService;
import io.github.aicyi.commons.security.token.exception.TokenException;
import io.github.aicyi.commons.security.token.exception.TokenExpiredException;
import io.github.aicyi.commons.security.token.exception.TokenInvalidException;
import io.github.aicyi.commons.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        try {
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

        } catch (Exception e) {

            throw new RuntimeException("create redis token failed", e);
        }
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

        // 2.删除Token信息
        revoke(token);

        // 3.构造TokenCreateRequest
        TokenCreateRequest<P> request = new TokenCreateRequest<>();

        request.setPrincipal(session.getPrincipal());

        request.setAttributes(session.getAttributes());

        request.setTtl(getRefreshTtl());

        request.setTimeUnit(getRefreshTimeUnit());

        // 4.创建Token
        return create(request);
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
     * 获取默认TTL
     */
    protected long getDefaultTtl(TokenCreateRequest<P> request) {

        return request.getTtl() > 0 ? request.getTimeUnit().toSeconds(request.getTtl()) : DEFAULT_TTL;
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
