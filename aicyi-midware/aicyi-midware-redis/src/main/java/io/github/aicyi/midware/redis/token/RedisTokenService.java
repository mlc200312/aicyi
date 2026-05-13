package io.github.aicyi.midware.redis.token;


import io.github.aicyi.commons.core.token.TokenService;

/**
 * Redis Token Service
 *
 * @param <P> Principal类型
 */
public interface RedisTokenService<P> extends TokenService<String, P> {
}