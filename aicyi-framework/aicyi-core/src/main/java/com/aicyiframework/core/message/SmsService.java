package com.aicyiframework.core.message;

import java.util.List;
import java.util.Map;

/**
 * 短信发送服务
 */
public interface SmsService {
    String send(
            List<String> phoneNumbers,
            String templateCode,
            Map<String, String> templateParams,
            String signName) throws MessageSendException;
}
