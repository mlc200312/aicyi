package io.github.aicyi.midware.message.core.template;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 10:14
 **/
public interface TemplateSender<T> {

    /**
     * 发送模版消息
     *
     * @param templateCode
     * @param templateParams
     * @param message
     * @return
     */
    boolean sendTemplate(String templateCode, Map<String, String> templateParams, T message);
}
