package io.github.aicyi.commons.core;

/**
 * @author Mr.Min
 * @description Web层通用响应接口
 * @date 21:31
 **/
public interface IResponse<D> extends IResult<String, D> {

    /**
     * 时间戳
     *
     * @return
     */
    Long getTimestamp();

    /**
     * 状态
     *
     * @return
     */
    boolean getStatus();
}
