package io.github.aicyi.midware.message.sms.sender.impl;

import io.github.aicyi.commons.core.template.TemplateEngineFactory;
import io.github.aicyi.midware.message.core.sender.TextMessageSender;
import io.github.aicyi.midware.message.core.template.TemplateProvider;
import io.github.aicyi.midware.message.sms.model.Carrier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Min
 * @description 邮件转短信服务
 * @date 11:46
 **/
public class DefaultSmsSender extends AbstractSmsSender {

    private final TextMessageSender textMessageSender;

    public DefaultSmsSender(TemplateProvider templateProvider, TemplateEngineFactory factory, TextMessageSender textMessageSender) {
        super(templateProvider, factory);
        this.textMessageSender = textMessageSender;
    }

    @Override
    public boolean send(String phoneNumber, String messageContent, String sign) {
        Carrier[] values = Carrier.values();
        List<String> phoneNumbers = new ArrayList<>();
        for (Carrier carrier : values) {
            phoneNumbers.add(phoneNumber + carrier.getGatewayDomain());
        }
        return textMessageSender.sendText(phoneNumbers, "Notification", messageContent);
    }
}
