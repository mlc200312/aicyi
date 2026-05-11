package io.github.aicyi.commons.codec.jwt;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.token.AbstractTokenManager;
import io.github.aicyi.commons.core.token.JwtTokenManager;
import io.github.aicyi.commons.core.token.TokenConfig;
import io.github.aicyi.commons.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Jwt令牌管理类
 * @date 15:42
 **/
public class DefaultJwtTokenManager<V extends IJWTInfo> extends AbstractTokenManager<V> implements JwtTokenManager<V> {

    private static final JsonCodec DEFAULT_JSON_CODEC = JsonUtils.getInstance();

    private final JsonCodec jsonCodec;
    private final Type type;

    public DefaultJwtTokenManager(TokenConfig config, JsonCodec jsonCodec, Type type) {
        super(config, new JwtTokenGenerator(config.getSigningKey(), config.getIssuer()));
        this.jsonCodec = jsonCodec;
        this.type = type;
    }

    public DefaultJwtTokenManager(TokenConfig config, Type type) {
        this(config, DEFAULT_JSON_CODEC, type);
    }

    @Override
    public String createToken(V value, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        String json = jsonCodec.toJson(value);
        Map<String, Object> addClaims = jsonCodec.fromJsonMap(json, String.class, Object.class);
        enhancedClaims.putAll(addClaims);
        return tokenGenerator.generateToken(enhancedClaims, timeout, unit);
    }

    @Override
    public Optional<V> parseJwtInfo(String token) {
        // 解析原Token中的声明
        Optional<Map<String, Object>> claims = parseToken(token);
        if (claims.isPresent()) {
            String json = jsonCodec.toJson(claims.get());
            return Optional.ofNullable(jsonCodec.fromJson(json, type));
        }
        return Optional.empty();
    }

    @Override
    public Set<String> getUserTokens(V value) {
        return Collections.emptySet();
    }

    @Override
    public void invalidateToken(String token) {
    }

    @Override
    public void invalidateAllTokens(V value) {
    }
}
