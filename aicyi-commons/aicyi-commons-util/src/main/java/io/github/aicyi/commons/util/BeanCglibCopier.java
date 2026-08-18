package io.github.aicyi.commons.util;

import io.github.aicyi.commons.lang.Assert;
import net.sf.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Mr.Min
 * @description Cglib Bean 拷贝
 * @date 2025/8/7
 **/
public final class BeanCglibCopier {

    /**
     * BeanCopier.create 会动态生成字节码，必须按源/目标类型缓存复用
     */
    private static final ConcurrentMap<String, BeanCopier> COPIER_CACHE = new ConcurrentHashMap<>();

    private BeanCglibCopier() {
    }

    /**
     * cglib 对象转换（仅拷贝同名同类型属性）
     *
     * @param source 源对象
     * @param target 目标类型
     * @param <K>    源类型
     * @param <T>    目标类型
     * @return 目标对象实例
     */
    public static <K, T> T copy(K source, Class<T> target) {
        Assert.notNull(source, "source");
        Assert.notNull(target, "target");

        BeanCopier copier = COPIER_CACHE.computeIfAbsent(
                source.getClass().getName() + "->" + target.getName(),
                key -> BeanCopier.create(source.getClass(), target, false)
        );

        try {
            T res = target.newInstance();
            copier.copy(source, res, null);
            return res;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "create target instance failed: " + target.getName(), e);
        }
    }
}
