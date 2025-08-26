package com.aicyiframework.integ.sms;

import com.aicyiframework.core.mail.EmailManager;
import com.aicyiframework.core.sms.SmsManager;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 11:46
 **/
public class EmailToSmsManager implements SmsManager {

    private EmailManager emailManager;

    public EmailManager getEmailManager() {
        return emailManager;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    @Override
    public boolean sendSms(List<String> phoneNumbers, String content, String templateId, Map<String, String> templateParams, String signName) {
        if (StringUtils.isNotBlank(content)) {
            return sendTextSms(phoneNumbers, content, signName);
        }
        return false;
    }

    @Override
    public boolean sendTextSms(List<String> phoneNumbers, String content, String signName) {
        List<String> toList = phoneNumbers.stream().map(phoneNumber -> phoneNumber + Carrier.CHINA_MOBILE.getGatewayDomain()).collect(Collectors.toList());
        return emailManager.sendTextEmail(toList, "Notification", content);
    }

    @Override
    public boolean sendTemplateSms(List<String> phoneNumbers, String templateId, Map<String, String> templateParams, String signName) {
        return false;
    }

    @Override
    public CompletableFuture<Boolean> sendSmsAsync(List<String> phoneNumbers, String content, String signName) {
        return CompletableFuture.supplyAsync(() -> sendTextSms(phoneNumbers, content, signName));
    }

    @Override
    public CompletableFuture<Boolean> sendSmsAsync(List<String> phoneNumbers, String templateId, Map<String, String> templateParams, String signName) {
        return CompletableFuture.supplyAsync(() -> sendTemplateSms(phoneNumbers, templateId, templateParams, signName));
    }
}
