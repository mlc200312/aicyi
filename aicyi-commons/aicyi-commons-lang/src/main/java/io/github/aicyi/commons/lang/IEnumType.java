package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description 枚举接口
 * @date 10:24
 **/
public interface IEnumType<K> {

    /**
     * 枚举代码
     *
     * @return 枚举代码
     */
    K getCode();

    /**
     * 描述
     *
     * @return 描述
     */
    String getDescription();
}
