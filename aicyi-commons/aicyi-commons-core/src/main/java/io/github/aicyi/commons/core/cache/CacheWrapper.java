package io.github.aicyi.commons.core.cache;

import io.github.aicyi.commons.lang.model.BaseBean;

/**
 * @author Mr.Min
 * @description 缓存值包装类：区分「真实空值」与「未命中」，用于缓存空值防穿透。
 * 无参构造为 JSON 反序列化必需，勿删。
 * @date 2026/5/22
 **/
public class CacheWrapper<T> extends BaseBean {
    private boolean nullValue;
    private T data;

    public CacheWrapper() {
    }

    public CacheWrapper(boolean nullValue, T data) {
        this.nullValue = nullValue;
        this.data = data;
    }

    public boolean isNullValue() {
        return nullValue;
    }

    public void setNullValue(boolean nullValue) {
        this.nullValue = nullValue;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> CacheWrapper<T> hit(T value) {
        return new CacheWrapper<>(false, value);
    }

    public static <T> CacheWrapper<T> miss() {
        return new CacheWrapper<>(true, null);
    }
}