package io.github.aicyi.midware.kit;

import io.github.aicyi.commons.core.message.MessageSendException;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import io.github.aicyi.midware.message.sms.AbstractSmsSender;
import io.github.aicyi.midware.message.sms.SmsProperties;

import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author Mr.Min
 * @description Twilio短信服务
 * @date 18:18
 **/
public class TwilioSmsSender extends AbstractSmsSender {

    private final String twilioNumber;

    public TwilioSmsSender(SmsProperties smsProperties, Map<String, String> template) {
        super(Executors.newFixedThreadPool(5), template);
        this.twilioNumber = smsProperties.getFrom();

        // 初始化Twilio客户端
        Twilio.init(smsProperties.getUsername(), smsProperties.getPassword());
    }

    public TwilioSmsSender(String accountSid, String authToken, String twilioNumber, Map<String, String> template) {
        super(Executors.newFixedThreadPool(5), template);
        this.twilioNumber = twilioNumber;

        // 初始化Twilio客户端
        Twilio.init(accountSid, authToken);
    }

    @Override
    public boolean send(String phoneNumber, String messageContent, String signName) {
        try {
            Message twilioMessage = new MessageCreator(
                    new PhoneNumber("+86" + phoneNumber),
                    new PhoneNumber(twilioNumber),
                    messageContent
            ).create();

            return twilioMessage.getErrorCode() == null;
        } catch (ApiException e) {
            throw new MessageSendException("短信发送失败：" + e.getMessage(), e);
        }
    }
}
