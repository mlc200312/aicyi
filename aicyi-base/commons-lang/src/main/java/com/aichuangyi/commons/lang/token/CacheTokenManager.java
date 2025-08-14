package com.aichuangyi.commons.lang.token;

import com.aichuangyi.core.token.TokenConfig;
import com.aichuangyi.core.token.TokenManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 通用缓存Token管理
 * @date 2025/8/12
 **/
public abstract class CacheTokenManager<U> implements TokenManager<String, U> {

    protected final TokenConfig config;

    public CacheTokenManager(TokenConfig config) {
        this.config = config;
    }

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
}