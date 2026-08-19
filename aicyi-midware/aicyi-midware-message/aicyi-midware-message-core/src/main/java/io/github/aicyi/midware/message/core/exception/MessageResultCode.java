package io.github.aicyi.midware.message.core.exception;

import io.github.aicyi.commons.lang.IResultCode;

/**
 * @author Mr.Min
 * @description 消息模块错误码枚举
 * <p>
 * 遵守 CommonResultCode 码段规范：5 位整数，前 3 位对齐 HTTP 状态码段，后 2 位为段内序号；
 * 消息发送类错误归入 4xx 业务段，避免被网关/监控误判为服务端故障
 * @date 2026/8/19
 **/
public enum MessageResultCode implements IResultCode {

    MESSAGE_PARAM_ERROR(40011, "Message Param Error"),
    MESSAGE_NOT_SUPPORTED(40012, "Message Type Not Supported"),
    MESSAGE_SEND_ERROR(40013, "Message Send Error"),
    TEMPLATE_RENDER_ERROR(40014, "Template Render Error"),
    TEMPLATE_NOT_FOUND(40411, "Message Template Not Found"),
    TEMPLATE_ENGINE_NOT_FOUND(40412, "Template Engine Not Found");

    private final Integer code;
    private final String message;

    MessageResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
