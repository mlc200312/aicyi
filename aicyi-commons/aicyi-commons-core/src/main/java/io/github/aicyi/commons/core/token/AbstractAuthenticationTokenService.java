package io.github.aicyi.commons.core.token;

import io.github.aicyi.commons.core.PrincipalSerializer;
import io.github.aicyi.commons.lang.exception.TokenExpiredException;
import io.github.aicyi.commons.lang.exception.TokenInvalidException;

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
            long refreshTokenTtl,
            TimeUnit refreshTokenTimeUnit,
            long accessTokenTtl,
            TimeUnit accessTokenTimeUnit
    ) {
        if (refreshTokenTtl <= 0 || accessTokenTtl <= 0) {
            throw new IllegalArgumentException("token ttl must be positive");
        }

        this.refreshTokenService = Objects.requireNonNull(refreshTokenService, "refreshTokenService");
        this.accessTokenProvider = Objects.requireNonNull(accessTokenProvider, "accessTokenProvider");
        this.serializer = Objects.requireNonNull(serializer, "serializer");
        this.refreshTokenTtl = refreshTokenTtl;
        this.refreshTokenTimeUnit = Objects.requireNonNull(refreshTokenTimeUnit, "refreshTokenTimeUnit");
        this.accessTokenTtl = accessTokenTtl;
        this.accessTokenTimeUnit = Objects.requireNonNull(accessTokenTimeUnit, "accessTokenTimeUnit");
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

        // 2. 生成AccessToken（不内嵌 refreshToken，避免 access token 泄露后被直接换取新 token）
        String accessToken = createAccessToken(principal, attributes);

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
        String accessToken = createAccessToken(principal, attributes);

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

        // 拷贝后过滤：provider 返回的 Map 可能是不可变实现，不得直接 remove
        Map<String, Object> result = new HashMap<>(attributes);

        result.remove(PRINCIPAL_CLAIM);

        return result;
    }

    protected String createAccessToken(P principal, Map<String, Object> attributes) {

        Map<String, Object> claims = attributes == null ? new HashMap<>() : new HashMap<>(attributes);

        String tokenId = generateTokenId();

        String principalJson = serializer.serialize(principal);

        claims.put(PRINCIPAL_CLAIM, principalJson);

        return accessTokenProvider.create(tokenId, claims, accessTokenTtl, accessTokenTimeUnit);
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
