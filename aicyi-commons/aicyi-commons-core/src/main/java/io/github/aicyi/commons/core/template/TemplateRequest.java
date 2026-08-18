package io.github.aicyi.commons.core.template;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 模版消息内容接口
 * @date 15:36
 **/
public interface TemplateRequest {

    /**
     * 获取模版ID
     *
     * @return 模板标识
     */
    String getTemplateId();

    /**
     * 获取模版参数
     *
     * @return 模板渲染参数
     */
    Map<String, Object> getTemplateParams();
}
