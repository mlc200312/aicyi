package com.aicyiframework.core.message;

import com.aicyiframework.core.exception.MessageSendException;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 短信发送服务
 * @date 2025/8/25
 **/
public interface SmsManager {
    String send(
            List<String> phoneNumbers,
            String templateCode,
            Map<String, String> templateParams,
            String signName) throws MessageSendException;
}
