package io.github.aicyi.commons.core.token;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token提供者接口：负责 Token 的创建与校验解析，不涉及存储与撤销（存储语义见 {@link TokenService}）
 *
 * @param <T> Token 载体类型
 * @author Mr.Min
 */
public interface TokenProvider<T> {

    /**
     * 创建 Token（有效期为实现默认策略）
     */
    T create(String tokenId, Map<String, Object> attributes);

    /**
     * 创建指定有效期的 Token
     *
     * @param ttl      有效期，null 表示实现默认策略
     * @param timeUnit ttl 的时间单位，ttl 为 null 时忽略
     */
    T create(String tokenId, Map<String, Object> attributes, Long ttl, TimeUnit timeUnit);

    boolean isValid(String token);

    Map<String, Object> parseClaims(String token);

    Map<String, Object> getAttributes(String token);

    <V> V getAttribute(String token, String attributeName);

    String getTokenId(String token);

    Date getExpiration(String token);

    /**
     * 获取剩余有效期
     *
     * @param unit 时间单位
     * @return 负值表示已过期；-1 表示未设置有效期（永久有效）
     */
    long getRemainingTtl(String token, TimeUnit unit);
}