package com.aichuangyi.core;

import java.util.Date;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 21:03
 **/
public abstract class AbstractTokenManager implements TokenManager {
    private static final long TOKEN_EXPIRE_MS = 3600000L;

    @Override
    public String createToken(String id, Map<String, Object> claimMap) {
        return createToken(id, getDefaultExpireAt(null), claimMap); // 1小时后过期,)
    }

    @Override
    public String refreshToken(String token) {
        return refreshToken(token, getDefaultExpireAt(null));
    }

    protected Date getDefaultExpireAt(Date date) {
        return date == null ? new Date(System.currentTimeMillis() + TOKEN_EXPIRE_MS) : date;
    }
}
