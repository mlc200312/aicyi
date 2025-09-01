package com.aichuangyi.commons.core.jwt;

import com.aichuangyi.commons.core.token.DefaultTokenManager;
import com.aichuangyi.commons.core.token.TokenConfig;
import com.aichuangyi.commons.lang.JsonConverter;
import com.aichuangyi.commons.lang.UserInfo;
import com.aichuangyi.commons.util.json.JacksonConverter;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Jwt令牌管理类
 * @date 15:42
 **/
public class JwtTokenManager<U extends UserInfo> extends DefaultTokenManager<U> {

    private final JsonConverter jsonConverter;
    private final Type type;

    public JwtTokenManager(TokenConfig tokenConfig, JsonConverter jsonConverter, Type type) {
        super(tokenConfig, new JwtTokenGenerator(tokenConfig.getSigningKey(), tokenConfig.getIssuer()));
        this.jsonConverter = jsonConverter;
        this.type = type;
    }

    public JwtTokenManager(TokenConfig tokenConfig, Type type) {
        this(tokenConfig, JacksonConverter.DEFAULT_SIMPLE_CONVERTER, type);
    }

    @Override
    public String createToken(U userInfo, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        String json = jsonConverter.toJson(userInfo);
        Map<String, Object> addClaims = jsonConverter.parseMap(json, Object.class);
        enhancedClaims.putAll(addClaims);

        return tokenGenerator.generateToken(enhancedClaims, timeout, unit);
    }

    @Override
    public Optional<U> parseUserInfo(String token) {
        // 解析原Token中的声明
        Optional<Map<String, Object>> claims = parseToken(token);

        if (claims.isPresent()) {

            String json = jsonConverter.toJson(claims.get());

            return Optional.ofNullable(jsonConverter.parse(json, type));
        }
        return Optional.empty();
    }

    @Override
    public Set<String> getUserTokens(U userInfo) {
        return Collections.emptySet();
    }

    @Override
    public void invalidateToken(String token) {
    }

    @Override
    public void invalidateAllTokens(U userInfo) {
    }
}
