package io.github.aicyi.commons.security.token;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.token.TokenCreateRequest;
import io.github.aicyi.commons.core.token.TokenProvider;
import io.github.aicyi.commons.util.Assert;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Jwt令牌管理类
 * @date 15:42
 **/
public class JwtTokenServiceImpl<P extends IJWTInfo> implements JwtTokenService<P> {

    /**
     * token提供者
     */
    private TokenProvider<String> tokenProvider;

    /**
     * JSON序列化
     */
    protected final JsonCodec jsonCodec;

    /**
     * Principal类型
     */
    private final Class<P> principalType;


    public JwtTokenServiceImpl(TokenProvider<String> tokenProvider, JsonCodec jsonCodec, Class<P> principalType) {
        this.tokenProvider = tokenProvider;
        this.jsonCodec = jsonCodec;
        this.principalType = principalType;
    }

    @Override
    public String create(TokenCreateRequest<P> request) {

        Assert.notNull(request, "tokenCreateRequest");

        Assert.notNull(request.getPrincipal(), "principal");

        long ttl = request.getTtl() > 0 ? request.getTimeUnit().toMillis(request.getTtl()) : DEFAULT_TTL;

        P principal = request.getPrincipal();

        String principalJson = jsonCodec.toJson(principal);

        Map<String, Object> attributes = new HashMap<>(request.getAttributes());

        attributes.put("principal", principalJson);

        return tokenProvider.create(request.getPrincipal().getId(), attributes, ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isValid(String token) {

        return tokenProvider.isValid(token);
    }

    @Override
    public P parsePrincipal(String token) {

        Map<String, Object> tokenInfo = parseClaims(token);

        String principalJson = MapUtils.getString(tokenInfo, "principal");

        return jsonCodec.fromJson(principalJson, principalType);
    }

    @Override
    public Map<String, Object> parseAttributes(String token) {

        Map<String, Object> tokenInfo = parseClaims(token);

        Map<String, Object> attributes = new HashMap<>();

        for (Map.Entry<String, Object> entry : tokenInfo.entrySet()) {

            String key = entry.getKey();

            if ("principal".equals(key)) {
                continue;
            }

            attributes.put(key, entry.getValue());
        }

        return attributes;
    }

    @Override
    public <V> V getAttribute(String token, String attributeName) {

        Map<String, Object> tokenInfo = parseClaims(token);

        return (V) tokenInfo.get(attributeName);
    }

    @Override
    public String refresh(String token) {

        P principal = parsePrincipal(token);

        Map<String, Object> attributes = parseAttributes(token);

        revoke(token);

        TokenCreateRequest<P> request = new TokenCreateRequest<>();

        request.setPrincipal(principal);

        request.setAttributes(attributes);

        request.setTtl(DEFAULT_REFRESH_TTL);

        request.setTimeUnit(TimeUnit.SECONDS);

        return create(request);
    }

    @Override
    public long getRemainingTtl(String token, TimeUnit unit) {

        return tokenProvider.getRemainingTtl(token, unit);
    }

    @Override
    public Set<String> getTokens(P principal) {
        return Collections.emptySet();
    }

    @Override
    public void revoke(String token) {
    }

    @Override
    public void revokeAll(P principal) {
    }

    /**
     * 获取Token信息（不存在则抛异常）
     *
     * @param token
     * @return
     */
    private Map<String, Object> parseClaims(String token) {

        return tokenProvider.parseClaims(token);
    }
}
