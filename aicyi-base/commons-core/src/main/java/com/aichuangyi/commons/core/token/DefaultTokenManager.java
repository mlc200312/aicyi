package com.aichuangyi.commons.core.token;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:20
 **/
public abstract class DefaultTokenManager<T, U> implements TokenManager<T, U> {

    protected final TokenConfig config;
    protected final TokenGenerator<T> tokenGenerator;

    public DefaultTokenManager(TokenConfig tokenConfig, TokenGenerator<T> tokenGenerator) {
        this.config = tokenConfig;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public T createToken(U userInfo) {
        return createToken(userInfo, config.getDefaultExpire(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    @Override
    public T createToken(U userInfo, long timeout, TimeUnit unit) {
        return createToken(userInfo, new HashMap<>(), timeout, unit);
    }

    @Override
    public T createToken(U userInfo, Map<String, Object> claims) {
        return createToken(userInfo, claims, config.getDefaultExpire(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean validateToken(T token) {
        // 验证JWT签名和过期时间
        return tokenGenerator.verifyToken(token);
    }

    @Override
    public Optional<Map<String, Object>> parseToken(T token) {
        // 验证Token有效性
        if (!validateToken(token)) {
            return Optional.empty();
        }

        // 解析原Token中的声明
        return tokenGenerator.parseToken(token);
    }

    @Override
    public Optional<Object> parseClaim(T token, String claimName) {
        // 解析原Token中的声明
        Optional<Map<String, Object>> claims = parseToken(token);
        if (claims.isPresent()) {
            return Optional.ofNullable(claims.get().get(claimName));
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> refreshToken(T token) {
        // 验证Token有效性
        if (!validateToken(token)) {
            return Optional.empty();
        }

        // 解析Token并获取用户信息
        Optional<U> userInfo = parseUserInfo(token);
        if (userInfo.isPresent()) {

            // 解析原Token中的声明
            Map<String, Object> claims = parseToken(token).get();

            // 使原Token失效
            invalidateToken(token);

            // 创建新Token
            T newToken = createToken(userInfo.get(), claims, config.getRefreshWindow(TimeUnit.SECONDS), TimeUnit.SECONDS);

            return Optional.of(newToken);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> getTokenExpire(T token, TimeUnit unit) {
        // 解析原Token中的有效期
        Optional<Date> expiration = tokenGenerator.getExpiration(token);

        if (expiration.isPresent()) {
            Date now = new Date();
            Date date = expiration.get();

            // 获取Token剩余有效期
            return date.after(now) ? Optional.of(unit.convert(date.getTime() - now.getTime(), TimeUnit.MILLISECONDS)) : Optional.empty();
        }
        return Optional.empty();
    }
}
