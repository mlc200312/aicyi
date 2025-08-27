package com.aicyiframework.integ.sms;

import com.aicyiframework.core.exception.MessageSendException;
import com.aicyiframework.core.sms.AbstractSmsManager;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author Mr.Min
 * @description Twilio短信服务
 * @date 18:18
 **/
public class TwilioSmsManager extends AbstractSmsManager {

    private final String twilioNumber;

    public TwilioSmsManager(String accountSid, String authToken, String twilioNumber, Map<String, String> template) {
        super(Executors.newFixedThreadPool(5), template);
        this.twilioNumber = twilioNumber;

        // 初始化Twilio客户端
        Twilio.init(accountSid, authToken);
    }

    @Override
    public boolean sendTextSms(String number, String content, String signName) {
        try {
            Message twilioMessage = new MessageCreator(
                    new PhoneNumber(number),
                    new PhoneNumber(twilioNumber),
                    content
            ).create();

            twilioMessage.getSid();
        } catch (ApiException e) {
            throw new MessageSendException("短信发送失败：" + e.getMessage(), e);
        }
        return true;
    }
}
