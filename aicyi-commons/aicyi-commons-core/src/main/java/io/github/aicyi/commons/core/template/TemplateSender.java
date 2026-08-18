package io.github.aicyi.commons.core.template;

/**
 * @author Mr.Min
 * @description 模板消息发送器：按模板渲染并发送消息（邮件/短信等渠道的模板发送契约）
 * @date 10:14
 **/
public interface TemplateSender<T extends TemplateRequest> {

    /**
     * 发送模版消息
     *
     * @param message 模板消息请求（含模板ID与参数）
     * @return 是否发送成功
     */
    boolean sendTemplate(T message);
}
