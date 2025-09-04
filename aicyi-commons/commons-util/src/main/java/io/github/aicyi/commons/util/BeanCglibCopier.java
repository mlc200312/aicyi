package io.github.aicyi.commons.util;

import net.sf.cglib.beans.BeanCopier;

/**
 * @author Mr.Min
 * @description Cglib Bean 拷贝
 * @date 2025/8/7
 **/
public class BeanCglibCopier {
    /**
     * cglib 对象转换
     *
     * @param source
     * @param target
     * @param <K>
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <K, T> T copy(K source, Class<T> target) throws IllegalAccessException, InstantiationException {
        BeanCopier copier = BeanCopier.create(source.getClass(), target, false);
        T res = target.newInstance();
        copier.copy(source, res, null);
        return res;
    }
}