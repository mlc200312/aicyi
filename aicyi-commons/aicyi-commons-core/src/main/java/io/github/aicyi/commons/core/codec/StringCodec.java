package io.github.aicyi.commons.core.codec;

/**
 * @author Mr.Min
 * @description 对象与字符串互转的编解码器契约（缓存值存储、身份序列化等场景通用）
 * @date 2026/8/14
 **/
public interface StringCodec<T> {

    /**
     * 序列化为字符串
     */
    String serialize(T value);

    /**
     * 从字符串反序列化
     */
    T deserialize(String value);
}
