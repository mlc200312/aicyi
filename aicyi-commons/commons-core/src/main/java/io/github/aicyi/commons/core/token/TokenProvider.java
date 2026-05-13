package io.github.aicyi.commons.core.token;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token提供者接口
 *
 * @param <T>
 */
public interface TokenProvider<T> {

    T create(String tokenId, Map<String, Object> attributes);

    String create(String tokenId, Map<String, Object> attributes, Long ttl, TimeUnit timeUnit);

    boolean isValid(String token);

    Map<String, Object> parseClaims(String token);

    String getTokenId(String token);

    Date getExpiration(String token);

    long getRemainingTtl(String token, TimeUnit unit);

    Map<String, Object> getAttributes(String token);

    <V> V getAttribute(String token, String attributeName);
}