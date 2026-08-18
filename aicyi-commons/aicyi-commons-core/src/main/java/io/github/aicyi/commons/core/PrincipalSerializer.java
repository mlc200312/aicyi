package io.github.aicyi.commons.core;

import io.github.aicyi.commons.core.codec.StringCodec;

/**
 * Principal序列化器（token 身份体系专用命名）。
 * <p>
 * 本质是通用的对象⇄字符串编解码，新代码请直接使用 {@link StringCodec}；
 * 本接口保留仅为 token 域的命名兼容。
 *
 * @param <P>
 */
public interface PrincipalSerializer<P> extends StringCodec<P> {
}