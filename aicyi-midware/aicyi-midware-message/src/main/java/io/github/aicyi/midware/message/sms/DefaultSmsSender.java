package io.github.aicyi.midware.message.sms;

import io.github.aicyi.midware.message.mail.MailSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 邮件转短信服务
 * @date 11:46
 **/
public class DefaultSmsSender extends AbstractSmsSender {

    private MailSender mailSender;

    public DefaultSmsSender(MailSender mailSender, Map<String, String> template) {
        super(template);
        this.mailSender = mailSender;
    }

    public DefaultSmsSender(MailSender mailSender) {
        super(new HashMap<>());
        this.mailSender = mailSender;
    }

    @Override
    public boolean send(String phoneNumber, String messageContent, String signName) {
        return mailSender.sendText(Arrays.asList(phoneNumber + Carrier.CHINA_MOBILE.getGatewayDomain()), "Notification", messageContent);
    }
}
