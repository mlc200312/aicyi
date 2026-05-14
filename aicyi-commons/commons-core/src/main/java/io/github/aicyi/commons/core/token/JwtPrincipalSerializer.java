package io.github.aicyi.commons.core.token;

/**
 * Principal序列化器
 *
 * @param <P>
 */
public interface JwtPrincipalSerializer<P> {

    /**
     * 序列化
     */
    String serialize(P principal);

    /**
     * 反序列化
     */
    P deserialize(String value);
}