package io.github.aicyi.commons.core;

/**
 * @author Mr.Min
 * @description 统一结果接口
 * @date 10:39
 **/
public interface IResult<C, D> extends ICodeType<C> {

    /**
     * 数据
     *
     * @return
     */
    D getData();
}
