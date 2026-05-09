package io.github.aicyi.midware.message.core.template;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 10:14
 **/
public interface TemplateSender<T> {

    /**
     * 发送模版消息
     *
     * @param message
     * @return
     */
    boolean sendTemplate(T message);
}
