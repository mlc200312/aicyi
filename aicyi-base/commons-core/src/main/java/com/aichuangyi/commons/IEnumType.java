package com.aichuangyi.commons;

/**
 * @author Mr.Min
 * @description 枚举接口
 * @date 10:24
 **/
public interface IEnumType<E> {

    /**
     * 枚举代码
     *
     * @return
     */
    E getCode();

    /**
     * 描述
     *
     * @return
     */
    String getDescription();
}
