package io.github.aicyi.commons.core;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 11:01
 **/
public interface ICodeType<C> {

    /**
     * 错误码
     *
     * @return
     */
    C getCode();

    /**
     * 错误信息
     *
     * @return
     */
    String getMessage();
}
