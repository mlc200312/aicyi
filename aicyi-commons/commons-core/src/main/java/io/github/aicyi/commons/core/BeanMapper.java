package io.github.aicyi.commons.core;

import java.util.Collection;
import java.util.List;

/**
 * @author Mr.Min
 * @description 统一的 mapper 接口定义
 * @date 17:06
 **/
public interface BeanMapper {

    /**
     * 对象映射
     */
    <S, T> T map(S source, Class<T> targetType);

    /**
     * 对象映射到已有对象
     */
    <S, T> void map(S source, T target);

    /**
     * 集合映射
     */
    <S, T> List<T> mapList(
            Collection<S> sourceList,
            Class<T> targetType
    );
}
