package io.github.aicyi.midware.message.sms.sender;

import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.midware.message.sms.model.Carrier;

import java.util.*;

/**
 * @author Mr.Min
 * @description 邮件转短信服务
 * @date 11:46
 **/
public class DefaultSmsSender extends AbstractSmsSender {

    private EmailSender emailSender;

    public DefaultSmsSender(EmailSender emailSender, Map<String, String> template) {
        super(template);
        this.emailSender = emailSender;
    }

    public DefaultSmsSender(EmailSender emailSender) {
        super(new HashMap<>());
        this.emailSender = emailSender;
    }

    @Override
    public boolean send(String phoneNumber, String messageContent, String signName) {
        Carrier[] values = Carrier.values();

        List<String> phoneNumbers = new ArrayList<>();
        for (Carrier carrier : values) {
            phoneNumbers.add(phoneNumber + carrier.getGatewayDomain());
        }

        return emailSender.sendText(phoneNumbers, "Notification", messageContent);
    }
}
