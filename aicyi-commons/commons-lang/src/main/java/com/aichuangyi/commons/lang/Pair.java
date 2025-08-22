package com.aichuangyi.commons.lang;

/**
 * @author Mr.Min
 * @description 键值对
 * @date 2020-05-21
 **/
public class Pair<K, V> extends BaseBean implements Cloneable {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

