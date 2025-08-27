package com.aicyiframework.integ.sms;

import com.aicyiframework.core.mail.EmailManager;
import com.aicyiframework.core.sms.AbstractSmsManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Mr.Min
 * @description 邮件转短信服务
 * @date 11:46
 **/
public class EmailToSmsManager extends AbstractSmsManager {

    private EmailManager emailManager;

    public EmailToSmsManager(ExecutorService executorService, Map<String, String> template) {
        super(executorService, template);
    }

    public EmailToSmsManager(Map<String, String> template) {
        super(template);
    }

    public EmailToSmsManager() {
        super(new HashMap<>());
    }

    public EmailManager getEmailManager() {
        return emailManager;
    }

    public void setEmailManager(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    @Override
    public boolean sendTextSms(String number, String content, String signName) {
        return emailManager.sendTextEmail(Arrays.asList(number + Carrier.CHINA_MOBILE.getGatewayDomain()), "Notification", content);
    }
}
