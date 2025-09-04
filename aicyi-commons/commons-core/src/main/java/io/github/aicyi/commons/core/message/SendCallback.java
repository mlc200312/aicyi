package io.github.aicyi.commons.core.message;

/**
 * @author Mr.Min
 * @description 发送结果回调接口
 * @date 2025/8/25
 **/
public interface SendCallback {
    /**
     * 成功
     *
     * @param result
     */
    void onComplete(SendResult result);

    /**
     * 失败
     *
     * @param e
     */
    void onError(Exception e);
}
