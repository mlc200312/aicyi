package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description Web层通用响应接口
 * @date 21:31
 **/
public interface IResponse<V> extends IResult<String, V> {

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
