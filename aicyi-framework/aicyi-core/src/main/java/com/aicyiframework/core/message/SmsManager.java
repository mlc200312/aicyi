package com.aicyiframework.core.message;

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
     * 发送短信
     *
     * @param phoneNumbers
     * @param content
     * @param templateId
     * @param templateParams
     * @param signName
     * @return
     */
    boolean sendSms(
            List<String> phoneNumbers,
            String content,
            String templateId,
            Map<String, String> templateParams,
            String signName);

    /**
     * 发送简单文本短信
     *
     * @param phoneNumbers
     * @param content
     * @param signName
     * @return
     * @
     */
    boolean sendTextSms(
            List<String> phoneNumbers,
            String content,
            String signName);

    /**
     * 发送模板短信
     *
     * @param phoneNumbers
     * @param templateId
     * @param templateParams
     * @param signName
     * @return
     * @
     */
    boolean sendTemplateSms(
            List<String> phoneNumbers,
            String templateId,
            Map<String, String> templateParams,
            String signName);

    /**
     * 异步发送短信
     *
     * @param phoneNumbers
     * @param content
     * @param signName
     * @return
     * @
     */
    CompletableFuture<Boolean> sendSmsAsync(
            List<String> phoneNumbers,
            String content,
            String signName);

    /**
     * 异步发送短信
     *
     * @param phoneNumbers
     * @param templateId
     * @param templateParams
     * @param signName
     * @return
     * @
     */
    CompletableFuture<Boolean> sendSmsAsync(
            List<String> phoneNumbers,
            String templateId,
            Map<String, String> templateParams,
            String signName);
}
