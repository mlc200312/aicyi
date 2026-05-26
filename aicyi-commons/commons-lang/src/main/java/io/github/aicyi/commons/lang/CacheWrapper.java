package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description Redis缓存包装类
 * @date 2026/5/22
 **/
public class CacheWrapper<T> {
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