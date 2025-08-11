package com.aichuangyi.commons;

import java.util.Date;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:12
 **/
public interface TokenManager {

    /**
     * 创建令牌
     *
     * @param id
     * @param claimMap
     * @return
     */
    String createToken(String id, Map<String, Object> claimMap);

    /**
     * 创建令牌
     *
     * @param id
     * @param expiresAt
     * @param claimMap
     * @return
     */
    String createToken(String id, Date expiresAt, Map<String, Object> claimMap);

    /**
     * 刷新令牌
     *
     * @param token
     * @return
     */
    String refreshToken(String token);

    /**
     * 刷新令牌
     *
     * @param token
     * @return
     */
    String refreshToken(String token, Date expiresAt);

    /**
     * 验证令牌
     *
     * @param token
     * @return
     */
    boolean verifyToken(String token);

    /**
     * 验证令牌并返回id
     *
     * @param token
     * @return
     */
    String verifyAndGetId(String token);

    /**
     * 验证令牌有效期
     *
     * @param token
     * @return
     */
    Date verifyAndGetExpire(String token);

    /**
     * 验证令牌并返回
     *
     * @param token
     * @param key
     * @param clazz
     * @return
     */
    <T> T verifyAndGet(String token, String key, Class<T> clazz);
}
