package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description Web层通用响应接口
 * @date 21:31
 **/
public interface IResponse {

    /**
     * 状态
     *
     * @return
     */
    boolean getStatus();

    /**
     * 返回码
     *
     * @return
     */
    String getCode();

    /**
     * 返回描述
     *
     * @return
     */
    String getMessage();
}
