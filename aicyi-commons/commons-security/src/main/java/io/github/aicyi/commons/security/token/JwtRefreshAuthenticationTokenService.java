package io.github.aicyi.commons.security.token;

import io.github.aicyi.commons.core.token.*;
import io.github.aicyi.commons.security.token.exception.TokenExpiredException;
import io.github.aicyi.commons.security.token.exception.TokenInvalidException;
import io.github.aicyi.commons.util.id.IdUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 刷新令牌认证Token服务
 * @date 16:13
 **/
public class JwtRefreshAuthenticationTokenService<P> implements AuthenticationTokenService<P> {

    /**
     * Principal Claim名称
     */
    private static final String PRINCIPAL_CLAIM = "principal";
    /**
     * RefreshToken Claim名称
     */
    private static final String REFRESH_TOKEN_CLAIM = "refresh_token";

    /**
     * 默认AccessToken有效期（分钟）
     */
    private static final long DEFAULT_ACCESS_TOKEN_TTL = 30;

    /**
     * 默认RefreshToken有效期（天）
     */
    private static final long DEFAULT_REFRESH_TOKEN_TTL = 7;

    /**
     * JWT提供者
     */
    private final JwtProvider<String> accessTokenProvider;

    /**
     * 令牌服务
     */
    private final TokenService<String, P> refreshTokenService;

    /**
     * JsonCodec
     */
    private final JwtPrincipalSerializer<P> serializer;

    /**
     * AccessToken有效期
     */
    private final long accessTokenTtl;

    /**
     * AccessToken时间单位
     */
    private final TimeUnit accessTokenTimeUnit;

    /**
     * RefreshToken有效期
     */
    private final long refreshTokenTtl;

    /**
     * RefreshToken时间单位
     */
    private final TimeUnit refreshTokenTimeUnit;

    public JwtRefreshAuthenticationTokenService(JwtProvider<String> accessTokenProvider, TokenService<String, P> refreshTokenService, JwtPrincipalSerializer<P> jwtPrincipalSerializer) {
        this(
                accessTokenProvider,
                refreshTokenService,
                jwtPrincipalSerializer,
                DEFAULT_ACCESS_TOKEN_TTL,
                TimeUnit.MINUTES,
                DEFAULT_REFRESH_TOKEN_TTL,
                TimeUnit.DAYS
        );
    }

    public JwtRefreshAuthenticationTokenService(
            JwtProvider<String> accessTokenProvider,
            TokenService<String, P> refreshTokenStore,
            JwtPrincipalSerializer<P> jwtPrincipalSerializer,
            long accessTokenTtl,
            TimeUnit accessTokenTimeUnit,
            long refreshTokenTtl,
            TimeUnit refreshTokenTimeUnit) {

        this.accessTokenProvider = Objects.requireNonNull(
                accessTokenProvider, "accessTokenProvider can not be null"
        );

        this.refreshTokenService = Objects.requireNonNull(
                refreshTokenStore, "refreshTokenService can not be null"
        );

        this.serializer = jwtPrincipalSerializer;

        this.accessTokenTtl = accessTokenTtl;

        this.accessTokenTimeUnit = accessTokenTimeUnit;

        this.refreshTokenTtl = refreshTokenTtl;

        this.refreshTokenTimeUnit = refreshTokenTimeUnit;
    }


    @Override
    public TokenPair createToken(P principal, Map<String, Object> attributes) {

        // 1. 生成RefreshToken(UUID)
        TokenCreateRequest<P> request = new TokenCreateRequest<>();

        request.setPrincipal(principal);

        request.setAttributes(attributes);

        request.setTtl(refreshTokenTtl);

        request.setTimeUnit(refreshTokenTimeUnit);

        String refreshToken = refreshTokenService.create(request);

        // 2. 生成JWT AccessToken
        String accessToken = createAccessToken(principal, attributes, refreshToken);

        // 3. 返回TokenPair
        return buildTokenPair(accessToken, refreshToken);
    }

    @Override
    public TokenPair refreshToken(String refreshToken) {

        // 1. 校验RefreshToken
        validateRefreshToken(refreshToken);

        // 2. 查询Redis
        P principal = refreshTokenService.parsePrincipal(refreshToken);

        Map<String, Object> attributes = refreshTokenService.parseAttributes(refreshToken);

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

        attributes.remove(PRINCIPAL_CLAIM);

        attributes.remove(REFRESH_TOKEN_CLAIM);

        return attributes;
    }

    @Override
    public void revokeToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return;
        }

        refreshTokenService.revoke(refreshToken);

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

        if (!refreshTokenService.isValid(refreshToken)) {

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

        TokenPair pair = new TokenPair();

        pair.setAccessToken(accessToken);

        pair.setRefreshToken(refreshToken);

        pair.setAccessTokenExpiresIn(accessTokenTimeUnit.toSeconds(accessTokenTtl));

        pair.setRefreshTokenExpiresIn(refreshTokenTimeUnit.toSeconds(refreshTokenTtl));

        return pair;
    }
}
