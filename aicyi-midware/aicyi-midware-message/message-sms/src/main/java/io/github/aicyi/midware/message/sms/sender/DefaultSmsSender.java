package io.github.aicyi.midware.message.sms.sender;

import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.sms.model.Carrier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 邮件转短信服务
 * @date 11:46
 **/
public class DefaultSmsSender extends AbstractSmsSender {

    private EmailSender EMailSender;

    public DefaultSmsSender(EmailSender EMailSender, Map<String, String> template) {
        super(template);
        this.EMailSender = EMailSender;
    }

    public DefaultSmsSender(EmailSender EMailSender) {
        super(new HashMap<>());
        this.EMailSender = EMailSender;
    }

    @Override
    public boolean send(String phoneNumber, String messageContent, String signName) {
        return EMailSender.sendText(Arrays.asList(phoneNumber + Carrier.CHINA_MOBILE.getGatewayDomain()), "Notification", messageContent);
    }
}
