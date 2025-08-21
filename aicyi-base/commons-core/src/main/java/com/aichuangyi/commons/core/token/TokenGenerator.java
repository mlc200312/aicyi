package com.aichuangyi.commons.core.token;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Token生成器接口
 *
 * @param <T>
 */
public interface TokenGenerator<T> {

    /**
     * 生成token(永久有效)
     *
     * @param id
     * @param claims
     * @return
     */
    T generateToken(String id, Map<String, Object> claims);

    /**
     * 生成token(永久有效)
     *
     * @param claims
     * @return
     */
    T generateToken(Map<String, Object> claims);

    /**
     * 生成token
     *
     * @param id
     * @param claims
     * @param timeout
     * @param unit
     * @return
     */
    T generateToken(String id, Map<String, Object> claims, long timeout, TimeUnit unit);

    /**
     * 生成token
     *
     * @param claims
     * @param timeout
     * @param unit
     * @return
     */
    T generateToken(Map<String, Object> claims, long timeout, TimeUnit unit);

    /**
     * 验证Token签名
     *
     * @param token Token
     * @return 是否有效
     */
    boolean verifyToken(T token);

    /**
     * 解析Token声明
     *
     * @param token Token
     * @return
     */
    Optional<Map<String, Object>> parseToken(T token);

    /**
     * 解析Token声明并获取Id
     *
     * @param token Token
     * @return
     */
    Optional<String> getId(T token);

    /**
     * 解析Token声明并返回userId
     *
     * @param token
     * @return
     */
    Optional<Date> getExpiration(T token);
}