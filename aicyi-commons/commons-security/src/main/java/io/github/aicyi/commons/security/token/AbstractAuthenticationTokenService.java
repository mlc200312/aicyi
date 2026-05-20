package io.github.aicyi.commons.security.token;

import io.github.aicyi.commons.core.token.*;
import io.github.aicyi.commons.security.token.exception.TokenExpiredException;
import io.github.aicyi.commons.security.token.exception.TokenInvalidException;
import io.github.aicyi.commons.security.token.jwt.JwtPrincipalSerializer;
import io.github.aicyi.commons.core.token.TokenProvider;
import io.github.aicyi.commons.security.token.jwt.JwtTokenProvider;
import io.github.aicyi.commons.util.id.IdUtils;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 抽象认证Token服务
 * @date 00:25
 **/
public abstract class AbstractAuthenticationTokenService<P> extends AbstractTokenService<P> implements AuthenticationTokenService<P> {

    /**
     * Principal Claim名称
     */
    private static final String PRINCIPAL_CLAIM = "principal";

    /**
     * RefreshToken Claim名称
     */
    private static final String REFRESH_TOKEN_CLAIM = "refresh_token";

    /**
     * JWT提供者
     */
    private final TokenProvider<String> accessTokenProvider;

    /**
     * JsonCodec
     */
    private final PrincipalSerializer<P> serializer;

    /**
     * AccessToken有效期
     */
    protected final long accessTokenTtl;

    /**
     * AccessToken时间单位
     */
    protected final TimeUnit accessTokenTimeUnit;

    public AbstractAuthenticationTokenService(SecretKey secretKey, String issuer, String subject, long refreshTokenTtl, TimeUnit refreshTokenTimeUnit, long accessTokenTtl, TimeUnit accessTokenTimeUnit, Class<? extends P> principalType) {
        super(refreshTokenTtl, refreshTokenTimeUnit);
        this.accessTokenProvider = new JwtTokenProvider(secretKey, issuer, subject);
        this.serializer = new JwtPrincipalSerializer<>(principalType);
        this.accessTokenTtl = accessTokenTtl;
        this.accessTokenTimeUnit = accessTokenTimeUnit;
    }

    public AbstractAuthenticationTokenService(String secretKey, String issuer, String subject, long refreshTokenTtl, TimeUnit refreshTokenTimeUnit, long accessTokenTtl, TimeUnit accessTokenTimeUnit, Class<? extends P> principalType) {
        super(refreshTokenTtl, refreshTokenTimeUnit);
        this.accessTokenProvider = new JwtTokenProvider(secretKey, issuer, subject);
        this.serializer = new JwtPrincipalSerializer<>(principalType);
        this.accessTokenTtl = accessTokenTtl;
        this.accessTokenTimeUnit = accessTokenTimeUnit;
    }

    public long getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public TimeUnit getAccessTokenTimeUnit() {
        return accessTokenTimeUnit;
    }

    @Override
    public TokenPair createToken(P principal, Map<String, Object> attributes) {

        // 1. 生成RefreshToken(UUID)
        TokenCreateRequest<P> request = new TokenCreateRequest<>();

        request.setPrincipal(principal);

        request.setAttributes(attributes);

        request.setTtl(getRefreshTtl());

        request.setTimeUnit(getRefreshTimeUnit());

        String refreshToken = create(request);

        // 2. 生成AccessToken
        String accessToken = createAccessToken(principal, attributes, refreshToken);

        // 3. 返回TokenPair
        return buildTokenPair(accessToken, refreshToken);
    }

    @Override
    public TokenPair refreshToken(String refreshToken) {

        // 1. 校验RefreshToken
        validateRefreshToken(refreshToken);

        // 2. 查询缓存对象
        P principal = super.parsePrincipal(refreshToken);

        Map<String, Object> attributes = super.parseAttributes(refreshToken);

        // 3. 重新生成AccessToken
        String accessToken = createAccessToken(principal, attributes, refreshToken);

        // 4. 返回TokenPair
        return buildTokenPair(accessToken, refreshToken);
    }

    @Override
    public boolean validateAccessToken(String accessToken) {

        return accessTokenProvider.isValid(accessToken);
    }

    @Override
    public P parsePrincipal(String accessToken) {

        String principalJson = accessTokenProvider.getAttribute(accessToken, PRINCIPAL_CLAIM);

        return serializer.deserialize(principalJson);
    }

    @Override
    public Map<String, Object> getAttributes(String accessToken) {

        Map<String, Object> attributes = accessTokenProvider.getAttributes(accessToken);

        if (attributes == null || attributes.isEmpty()) {
            return Collections.emptyMap();
        }

        attributes.remove(PRINCIPAL_CLAIM);

        attributes.remove(REFRESH_TOKEN_CLAIM);

        return attributes;
    }

    @Override
    public Set<String> getRefreshTokens(P principal) {

        return getTokens(principal);
    }

    @Override
    public void revokeToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return;
        }

        this.revoke(refreshToken);
    }

    protected String createAccessToken(P principal, Map<String, Object> attributes, String refreshToken) {

        attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);

        String tokenId = IdUtils.generateV7Id();

        String principalJson = serializer.serialize(principal);

        attributes.put(PRINCIPAL_CLAIM, principalJson);

        attributes.put(REFRESH_TOKEN_CLAIM, refreshToken);

        return accessTokenProvider.create(tokenId, attributes, accessTokenTtl, accessTokenTimeUnit);
    }

    /**
     * 校验RefreshToken
     *
     * @param refreshToken
     */
    protected void validateRefreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {

            throw new TokenInvalidException("refresh token can not be blank");
        }

        if (!this.isValid(refreshToken)) {

            throw new TokenExpiredException("refresh token expired");
        }
    }

    /**
     * 构建TokenPair
     *
     * @param accessToken
     * @param refreshToken
     * @return
     */
    protected TokenPair buildTokenPair(String accessToken, String refreshToken) {

        TokenPair tokenPair = new TokenPair();

        tokenPair.setAccessToken(accessToken);

        tokenPair.setRefreshToken(refreshToken);

        tokenPair.setAccessTokenExpiresIn(getAccessTokenTimeUnit().toSeconds(getAccessTokenTtl()));

        tokenPair.setRefreshTokenExpiresIn(getRefreshTimeUnit().toSeconds(getRefreshTtl()));

        return tokenPair;
    }
}
