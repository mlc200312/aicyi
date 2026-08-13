package io.github.aicyi.commons.core.token;

import io.github.aicyi.commons.core.PrincipalSerializer;
import io.github.aicyi.commons.lang.exception.TokenExpiredException;
import io.github.aicyi.commons.lang.exception.TokenInvalidException;
import io.github.aicyi.commons.lang.model.TokenCreateRequest;
import io.github.aicyi.commons.lang.model.TokenPair;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 抽象认证Token服务
 * @date 00:25
 **/
public abstract class AbstractAuthenticationTokenService<P> implements AuthenticationTokenService<P> {

    /**
     * Principal Claim名称
     */
    private static final String PRINCIPAL_CLAIM = "principal";

    /**
     * RefreshToken Claim名称
     */
    private static final String REFRESH_TOKEN_CLAIM = "refresh_token";

    /**
     * 刷新Token服务
     */
    private final TokenService<String, P> refreshTokenService;

    /**
     * 访问Token服务
     */
    private final TokenProvider<String> accessTokenProvider;

    /**
     * Principal序列化器
     */
    private final PrincipalSerializer<P> serializer;

    /**
     * RefreshToken有效期
     */
    protected final long refreshTokenTtl;

    /**
     * RefreshToken时间单位
     */
    protected final TimeUnit refreshTokenTimeUnit;

    /**
     * AccessToken有效期
     */
    protected final long accessTokenTtl;

    /**
     * AccessToken时间单位
     */
    protected final TimeUnit accessTokenTimeUnit;

    public AbstractAuthenticationTokenService(
            TokenService<String, P> refreshTokenService,
            TokenProvider<String> accessTokenProvider,
            PrincipalSerializer<P> serializer,
            Long refreshTokenTtl,
            TimeUnit refreshTokenTimeUnit,
            Long accessTokenTtl,
            TimeUnit accessTokenTimeUnit
    ) {
        this.refreshTokenService = refreshTokenService;
        this.accessTokenProvider = accessTokenProvider;
        this.serializer = serializer;
        this.refreshTokenTtl = refreshTokenTtl;
        this.refreshTokenTimeUnit = refreshTokenTimeUnit;
        this.accessTokenTtl = accessTokenTtl;
        this.accessTokenTimeUnit = accessTokenTimeUnit;
    }

    public long getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public TimeUnit getRefreshTokenTimeUnit() {
        return refreshTokenTimeUnit;
    }

    public long getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public TimeUnit getAccessTokenTimeUnit() {
        return accessTokenTimeUnit;
    }

    protected String generateTokenId() {
        return UUID.randomUUID().toString();
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
        P principal = refreshTokenService.parsePrincipal(refreshToken);

        Map<String, Object> attributes = refreshTokenService.parseAttributes(refreshToken);

        // 3. 重新生成AccessToken
        String accessToken = createAccessToken(principal, attributes, refreshToken);

        // 4. 返回TokenPair
        return buildTokenPair(accessToken, refreshToken);
    }

    @Override
    public Set<String> getRefreshTokens(P principal) {

        return refreshTokenService.getTokens(principal);
    }

    @Override
    public void revokeToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return;
        }

        refreshTokenService.revoke(refreshToken);
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

    protected String createAccessToken(P principal, Map<String, Object> attributes, String refreshToken) {

        attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);

        String tokenId = generateTokenId();

        String principalJson = serializer.serialize(principal);

        attributes.put(PRINCIPAL_CLAIM, principalJson);

        attributes.put(REFRESH_TOKEN_CLAIM, refreshToken);

        return accessTokenProvider.create(tokenId, attributes, accessTokenTtl, accessTokenTimeUnit);
    }

    /**
     * 校验RefreshToken
     *
     * @param refreshToken 刷新Token
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
     * @param accessToken  访问Token
     * @param refreshToken 刷新Token
     * @return TokenPair
     */
    protected TokenPair buildTokenPair(String accessToken, String refreshToken) {

        TokenPair tokenPair = new TokenPair();

        tokenPair.setAccessToken(accessToken);

        tokenPair.setRefreshToken(refreshToken);

        tokenPair.setAccessTokenExpiresIn(accessTokenTimeUnit.toSeconds(accessTokenTtl));

        tokenPair.setRefreshTokenExpiresIn(refreshTokenTimeUnit.toSeconds(refreshTokenTtl));

        return tokenPair;
    }
}
