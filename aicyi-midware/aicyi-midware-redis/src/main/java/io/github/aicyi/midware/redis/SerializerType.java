package io.github.aicyi.midware.redis;

/**
 * @author Mr.Min
 * @description 序列化类型枚举
 * @date 2026/5/22
 **/
public enum SerializerType {

    /**
     * JDK 原生序列化：存在反序列化安全风险与类版本兼容问题，请优先使用 JSON
     */
    @Deprecated
    JDK,

    JSON
}