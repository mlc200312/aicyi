package io.github.aicyi.commons.core.token;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Token 服务接口
 * @date 2025/8/12
 **/
public interface TokenService<T, P> {

    /**
     * 创建 Token 对象
     *
     * @param request Token创建请求
     * @return Token
     */
    T create(TokenCreateRequest<P> request);

    /**
     * 校验 Token 是否有效
     *
     * @param token Token
     * @return true-有效
     */
    boolean isValid(String token);

    /**
     * 解析 Token 主体信息
     *
     * @param token Token
     * @return 主体信息
     */
    P parsePrincipal(String token);

    /**
     * 解析 Token 属性
     *
     * @param token Token
     * @return Token属性
     */
    Map<String, Object> parseAttributes(String token);

    /**
     * 获取指定 Token 属性
     *
     * @param token         Token
     * @param attributeName 属性名称
     * @param <V>           属性值类型
     * @return 属性值
     */
    <V> V getAttribute(String token, String attributeName);

    /**
     * 刷新 Token
     *
     * @param token 原Token
     * @return 新Token
     */
    T refresh(String token);

    /**
     * 获取 Token 剩余有效期
     *
     * @param token Token
     * @param unit  时间单位
     * @return 剩余有效时间
     */
    long getRemainingTtl(String token, TimeUnit unit);

    /**
     * 获取主体所有有效 Token
     *
     * @param principal 主体信息
     * @return Token集合
     */
    Set<String> getTokens(P principal);

    /**
     * 撤销 Token
     *
     * @param token Token
     */
    void revoke(String token);

    /**
     * 撤销主体所有 Token
     *
     * @param principal 主体信息
     */
    void revokeAll(P principal);
}