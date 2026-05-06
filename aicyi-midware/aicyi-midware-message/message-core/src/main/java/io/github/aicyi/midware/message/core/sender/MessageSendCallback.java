package io.github.aicyi.midware.message.core.sender;

import io.github.aicyi.midware.message.core.model.MessageSendResult;

/**
 * @author Mr.Min
 * @description 发送结果回调接口
 * @date 2025/8/25
 **/
public interface MessageSendCallback {
    /**
     * 成功
     *
     * @param result
     */
    void onComplete(MessageSendResult result);

    /**
     * 失败
     *
     * @param e
     */
    void onError(Exception e);
}
