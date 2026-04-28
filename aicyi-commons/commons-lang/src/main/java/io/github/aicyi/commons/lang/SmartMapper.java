package io.github.aicyi.commons.lang;

import java.util.Collection;
import java.util.List;

/**
 * @author Mr.Min
 * @description 统一的 mapper 接口定义
 * @date 17:06
 **/
public interface SmartMapper {

    <S, D> D map(S src, Class<D> destType);

    <S, D> D map(S src, D dest);

    <S, D> List<D> mapAsList(Collection<S> src, Class<D> destType);
}
