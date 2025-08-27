package com.aicyiframework.core.sms;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Mr.Min
 * @description 短信发送服务
 * @date 2025/8/25
 **/
public interface SmsManager {
    /**
     * 发送简单文本短信
     *
     * @param number
     * @param content
     * @param signName
     * @return
     * @
     */
    boolean sendTextSms(String number, String content, String signName);

    /**
     * 发送模板短信
     *
     * @param number
     * @param templateId
     * @param templateParams
     * @param signName
     * @return
     * @
     */
    boolean sendTemplateSms(String number, String templateId, Map<String, String> templateParams, String signName);

    /**
     * 异步发送短信
     *
     * @param numbers
     * @param content
     * @param signName
     * @return
     * @
     */
    CompletableFuture<Boolean> sendTextSmsAsync(List<String> numbers, String content, String signName);

    /**
     * 异步发送短信
     *
     * @param numbers
     * @param templateId
     * @param templateParams
     * @param signName
     * @return
     * @
     */
    CompletableFuture<Boolean> sendTemplateSmsAsync(List<String> numbers, String templateId, Map<String, String> templateParams, String signName);
}
