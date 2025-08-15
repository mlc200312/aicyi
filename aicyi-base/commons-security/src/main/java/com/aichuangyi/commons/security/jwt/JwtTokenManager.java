package com.aichuangyi.commons.security.jwt;

import com.aichuangyi.commons.core.api.JsonConverter;
import com.aichuangyi.commons.core.token.DefaultTokenManager;
import com.aichuangyi.commons.core.token.TokenConfig;
import com.aichuangyi.commons.lang.UserInfo;
import com.aichuangyi.commons.util.json.JacksonConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:42
 **/
public class JwtTokenManager<U extends UserInfo> extends DefaultTokenManager<String, U> {

    // Json序列化
    private static final JacksonConverter INSTANCE = new JacksonConverter();

    static {
        INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected final JsonConverter jsonConverter;

    public JwtTokenManager(TokenConfig tokenConfig, JsonConverter jsonConverter) {
        super(tokenConfig, new JwtTokenGenerator(tokenConfig.getSigningKey(), tokenConfig.getIssuer()));
        this.jsonConverter = jsonConverter;
    }

    public JwtTokenManager(TokenConfig tokenConfig) {
        this(tokenConfig, INSTANCE);
    }

    @Override
    public String createToken(U userInfo, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        String json = jsonConverter.toJson(userInfo);
        Map<String, Object> addClaims = jsonConverter.parseMap(json, Object.class);
        enhancedClaims.putAll(addClaims);

        // 生成Token
        return tokenGenerator.generateToken(userInfo.getUserId(), enhancedClaims, timeout, unit);
    }

    @Override
    public Optional<U> parseUserInfo(String token) {

        // 解析原Token中的声明
        Optional<Map<String, Object>> claims = parseToken(token);

        if (claims.isPresent()) {

            String json = jsonConverter.toJson(claims.get());

            return Optional.ofNullable(jsonConverter.parse(json, UserInfo.class));
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
    public void invalidateUserTokens(U userInfo) {
    }
}
