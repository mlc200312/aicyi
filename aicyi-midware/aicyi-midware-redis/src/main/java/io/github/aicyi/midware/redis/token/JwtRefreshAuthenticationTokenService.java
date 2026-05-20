package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.security.token.AbstractAuthenticationTokenService;
import io.github.aicyi.commons.security.token.TokenSession;
import io.github.aicyi.commons.security.token.exception.TokenInvalidException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 刷新Token服务
 * @date 14:55
 **/
public class JwtRefreshAuthenticationTokenService<P extends IJWTInfo> extends AbstractAuthenticationTokenService<P> {

    private MultiRedisTokenServiceImpl<P> redisTokenService;

    public JwtRefreshAuthenticationTokenService(AuthenticationConfig config, StringRedisTemplate redisTemplate, Class<? extends P> principalType) {
        super(
                config.getSecretKey(),
                config.getIssuer(),
                config.getIssuer(),
                config.getRefreshTokenTtl(),
                config.getRefreshTokenTimeUnit(),
                config.getAccessTokenTtl(),
                config.getAccessTokenTimeUnit(),
                principalType
        );
        this.redisTokenService = new MultiRedisTokenServiceImpl<>(
                redisTemplate,
                principalType,
                config.getRefreshTokenTtl(),
                config.getRefreshTokenTimeUnit(),
                config.isMultiTokenAllowed(),
                config.getMultiTokenCount()
        );
    }

    @Override
    protected String createToken() {
        return redisTokenService.createToken();
    }

    @Override
    protected String getTokenId(String token) {
        return redisTokenService.getTokenId(token);
    }

    @Override
    protected String getPrincipalId(P principal) {
        return redisTokenService.getPrincipalId(principal);
    }

    @Override
    protected void saveTokenSession(TokenSession<P> session, long ttlSeconds) {
        redisTokenService.saveTokenSession(session, ttlSeconds);
    }

    @Override
    protected void cachePrincipalToken(P principal, String token, long ttlSeconds) {
        redisTokenService.cachePrincipalToken(principal, token, ttlSeconds);
    }

    @Override
    protected TokenSession<P> getTokenSession(String token) throws TokenInvalidException {
        return redisTokenService.getTokenSession(token);
    }

    @Override
    public long getRemainingTtl(String token, TimeUnit unit) {
        return redisTokenService.getRemainingTtl(token, unit);
    }

    @Override
    public Set<String> getTokens(P principal) {
        return redisTokenService.getTokens(principal);
    }

    @Override
    public void revoke(String token) {
        redisTokenService.revoke(token);
    }

    @Override
    public void revokeAll(P principal) {
        redisTokenService.revokeAll(principal);
    }
}
