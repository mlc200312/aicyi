package io.github.aicyi.midware.message.core.template;

import io.github.aicyi.midware.message.core.model.MessageContent;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 模版消息内容接口
 * @date 15:36
 **/
public interface TemplateMessage<T> extends MessageContent<T> {

    /**
     * 获取模版ID
     *
     * @return
     */
    String getTemplateId();

    /**
     * 获取模版参数
     *
     * @return
     */
    Map<String, Object> getTemplateParams();
}
