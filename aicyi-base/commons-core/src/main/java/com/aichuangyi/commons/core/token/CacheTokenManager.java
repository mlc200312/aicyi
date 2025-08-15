package com.aichuangyi.commons.core.token;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 通用缓存Token管理
 * @date 2025/8/12
 **/
public abstract class CacheTokenManager<T, U> extends DefaultTokenManager<T, U> {

    public CacheTokenManager(TokenConfig tokenConfig, TokenGenerator<T> tokenGenerator) {
        super(tokenConfig, tokenGenerator);
    }

    /**
     * 获取缓存
     *
     * @param token
     * @return
     */
    public abstract U getCache(T token);

    /**
     * 是否存在缓存
     *
     * @param token
     * @return
     */
    public abstract boolean hashCache(T token);

    /**
     * 获取缓存有效期
     *
     * @param token
     * @param unit
     * @return
     */
    public abstract long getCacheExpire(T token, TimeUnit unit);

    /**
     * 删除缓存
     *
     * @param token
     */
    public abstract void removeCache(T token);

    @Override
    public boolean validateToken(T token) {

        // 验证JWT签名和过期时间
        if (tokenGenerator.verifyToken(token)) {

            // 检查Token是否存在
            return hashCache(token);
        }

        return false;
    }

    @Override
    public Optional<U> parseUserInfo(T token) {

        // 验证Token有效性
        if (!validateToken(token)) {
            return Optional.empty();
        }

        // 获取用户信息
        U userInfo = getCache(token);

        return Optional.ofNullable(userInfo);
    }

    @Override
    public Optional<Long> getTokenExpire(T token, TimeUnit unit) {
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
        Set<T> userTokens = getUserTokens(userInfo);

        // 使Token失效
        userTokens.forEach(this::invalidateToken);
    }
}