package io.github.aicyi.commons.core.message;


/**
 * @author Mr.Min
 * @description 发送结果回调接口
 * @date 2025/8/25
 **/
public interface MessageSendCallback {
    /**
     * 发送成功回调
     *
     * @param result 发送结果
     */
    void onComplete(MessageSendResult result);

    /**
     * 发送失败回调
     *
     * @param e 发送异常
     */
    void onError(Exception e);
}
