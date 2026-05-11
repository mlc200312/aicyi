package io.github.aicyi.commons.core;

/**
 * @author Mr.Min
 * @description 枚举接口
 * @date 10:24
 **/
public interface IEnumType<C> {

    /**
     * 枚举代码
     *
     * @return
     */
    C getCode();

    /**
     * 描述
     *
     * @return
     */
    String getDescription();
}
