package io.github.aicyi.commons.core.jwt;

import io.github.aicyi.commons.core.token.DefaultTokenManager;
import io.github.aicyi.commons.core.token.TokenConfig;
import io.github.aicyi.commons.lang.IJWTInfo;
import io.github.aicyi.commons.lang.JsonCodec;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Jwt令牌管理类
 * @date 15:42
 **/
public class JwtTokenManager<V extends IJWTInfo> extends DefaultTokenManager<V> implements IJwtTokenManager<V> {

    private final JsonCodec jsonMapper;
    private final Type type;

    public JwtTokenManager(TokenConfig tokenConfig, JsonCodec jsonMapper, Type type) {
        super(tokenConfig, new JwtTokenGenerator(tokenConfig.getSigningKey(), tokenConfig.getIssuer()));
        this.jsonMapper = jsonMapper;
        this.type = type;
    }

    public JwtTokenManager(TokenConfig tokenConfig, Type type) {
        this(tokenConfig, JacksonJsonCodec.DEFAULT, type);
    }

    @Override
    public String createToken(V value, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        String json = jsonMapper.toJson(value);
        Map<String, Object> addClaims = jsonMapper.fromJsonMap(json, String.class, Object.class);
        enhancedClaims.putAll(addClaims);
        return tokenGenerator.generateToken(enhancedClaims, timeout, unit);
    }

    @Override
    public Optional<V> parseJwtInfo(String token) {
        // 解析原Token中的声明
        Optional<Map<String, Object>> claims = parseToken(token);
        if (claims.isPresent()) {
            String json = jsonMapper.toJson(claims.get());
            return Optional.ofNullable(jsonMapper.fromJson(json, type));
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
