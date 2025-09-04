package io.github.aicyi.commons.core.token;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用Token管理接口
 *
 * @author Mr.Min
 * @description 业务描述
 * @date 2025/8/12
 **/
public interface TokenManager<T, U> {

    /**
     * 创建Token
     *
     * @param userInfo 用户信息
     * @return 生成的Token
     */
    T createToken(U userInfo);

    /**
     * 创建Token并设置过期时间
     *
     * @param userInfo 用户信息
     * @param timeout  过期时间
     * @param unit     时间单位
     * @return 生成的Token
     */
    T createToken(U userInfo, long timeout, TimeUnit unit);

    /**
     * 创建Token并添加自定义声明
     *
     * @param userInfo 用户信息
     * @param claims   自定义声明
     * @return 生成的Token
     */
    T createToken(U userInfo, Map<String, Object> claims);

    /**
     * 创建Token（完整参数）
     *
     * @param userInfo 用户信息
     * @param claims   自定义声明
     * @param timeout  过期时间
     * @param unit     时间单位
     * @return 生成的Token
     */
    T createToken(U userInfo, Map<String, Object> claims, long timeout, TimeUnit unit);

    /**
     * 验证Token有效性
     *
     * @param token 待验证Token
     * @return 是否有效
     */
    boolean validateToken(T token);

    /**
     * 从Token中解析用户信息
     *
     * @param token Token
     * @return 用户信息
     */
    Optional<U> parseUserInfo(T token);

    /**
     * 从Token中解析声明
     *
     * @param token Token
     * @return 声明值
     */
    Optional<Map<String, Object>> parseToken(T token);

    /**
     * 从Token中解析特定声明
     *
     * @param token     Token
     * @param claimName 声明名称
     * @return 声明值
     */
    Optional<Object> parseClaim(T token, String claimName);

    /**
     * 刷新Token
     *
     * @param token 原Token
     * @return 新Token
     */
    Optional<T> refreshToken(T token);

    /**
     * 获取Token剩余有效时间
     *
     * @param token Token
     * @param unit  时间单位
     * @return 剩余时间
     */
    Optional<Long> getTokenExpire(T token, TimeUnit unit);

    /**
     * 获取用户的所有有效Token
     *
     * @param userInfo 用户信息
     * @return Token集合
     */
    Set<T> getUserTokens(U userInfo);

    /**
     * 使Token失效
     *
     * @param token 要失效的Token
     */
    void invalidateToken(T token);

    /**
     * 使某用户的所有Token失效
     *
     * @param userInfo 用户信息
     */
    void invalidateAllTokens(U userInfo);
}