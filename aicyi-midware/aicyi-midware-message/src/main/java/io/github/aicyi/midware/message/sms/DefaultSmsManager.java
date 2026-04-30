package io.github.aicyi.midware.message.sms;

import io.github.aicyi.midware.message.mail.MailManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 邮件转短信服务
 * @date 11:46
 **/
public class DefaultSmsManager extends AbstractSmsManager {

    private MailManager mailManager;

    public DefaultSmsManager(MailManager mailManager, Map<String, String> template) {
        super(template);
        this.mailManager = mailManager;
    }

    public DefaultSmsManager(MailManager mailManager) {
        super(new HashMap<>());
        this.mailManager = mailManager;
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    @Override
    public boolean sendTextSms(String number, String content, String signName) {
        return mailManager.sendTextEmail(Arrays.asList(number + Carrier.CHINA_MOBILE.getGatewayDomain()), "Notification", content);
    }
}
