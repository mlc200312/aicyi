package com.aichuangyi.commons.core.token;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 通用缓存Token管理
 * @date 2025/8/12
 **/
public abstract class AbstractCacheTokenManager<U> implements TokenManager<String, U> {

    protected final TokenConfig config;

    public AbstractCacheTokenManager(TokenConfig config) {
        this.config = config;
    }

    /**
     * 获取Token生成器实例
     *
     * @return
     */
    protected abstract TokenGenerator<String> getTokenGenerator();

    /**
     * 获取缓存
     *
     * @param token
     * @return
     */
    public abstract U getCache(String token);

    /**
     * 是否存在缓存
     *
     * @param token
     * @return
     */
    public abstract boolean hashCache(String token);

    /**
     * 获取缓存有效期
     *
     * @param token
     * @param unit
     * @return
     */
    public abstract long getCacheExpire(String token, TimeUnit unit);

    /**
     * 删除缓存
     *
     * @param token
     */
    public abstract void removeCache(String token);

    @Override
    public String createToken(U userInfo) {

        return createToken(userInfo, config.getDefaultExpire(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    @Override
    public String createToken(U userInfo, long timeout, TimeUnit unit) {

        return createToken(userInfo, new HashMap<>(), timeout, unit);
    }

    @Override
    public String createToken(U userInfo, Map<String, Object> claims) {

        return createToken(userInfo, claims, config.getDefaultExpire(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean validateToken(String token) {

        // 验证JWT签名和过期时间
        if (getTokenGenerator().verifyToken(token)) {

            // 检查Token是否存在
            return hashCache(token);
        }

        return false;
    }

    @Override
    public Optional<U> parseUserInfo(String token) {
        if (!validateToken(token)) {
            return Optional.empty();
        }

        // 获取用户信息
        U userInfo = getCache(token);

        return Optional.ofNullable(userInfo);
    }

    @Override
    public Optional<Map<String, Object>> parseToken(String token) {
        if (!validateToken(token)) {
            return Optional.empty();
        }

        // 解析原Token中的声明
        Optional<Map<String, Object>> claims = getTokenGenerator().parseToken(token);

        return claims;
    }

    @Override
    public Optional<Object> parseClaim(String token, String claimName) {

        // 解析原Token中的声明
        Optional<Map<String, Object>> claims = parseToken(token);
        if (claims.isPresent()) {
            return Optional.ofNullable(claims.get().get(claimName));
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> refreshToken(String token) {
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
            String newToken = createToken(userInfo.get(), claims, config.getRefreshWindow(TimeUnit.SECONDS), TimeUnit.SECONDS);

            return Optional.of(newToken);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> getTokenExpire(String token, TimeUnit unit) {
        if (!validateToken(token)) {
            return Optional.empty();
        }

        // 获取Token的有效期
        long expire = getCacheExpire(token, unit);

        return Optional.of(expire);
    }

    @Override
    public void invalidateUserTokens(U userInfo) {

        // 获取用户所有的Token
        Set<String> userTokens = getUserTokens(userInfo);

        // 使Token失效
        userTokens.forEach(this::invalidateToken);
    }
}