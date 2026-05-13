package io.github.aicyi.example.domain.constants;

import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.example.domain.type.CaptchaType;
import io.github.aicyi.midware.message.mail.model.MailMessage;
import io.github.aicyi.midware.message.sms.model.SmsMessage;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:07
 **/
public class Constants {

    public static final Long CAPTCHA_EXPIRE = 3 * 60 * 1000L;
    public static final Long DEFAULT_CAPTCHA_EXPIRE = 15 * 60 * 1000L;

    public static String getCaptchaKey(String uuid) {
        return String.format("captcha:", uuid);
    }

    public static final Long getCaptchaExpire(CaptchaType captchaType) {
        switch (captchaType) {
            default:
                return DEFAULT_CAPTCHA_EXPIRE;
        }
    }

    public static String getCaptchaKey(CaptchaType captchaType, String uuid) {
        switch (captchaType) {
            case IMAGE_CAPTCHA_TYPE:
                return getCaptchaKey(uuid);
            case LOGIN_CAPTCHA_TYPE:
                return String.format("email:captcha:login:", uuid);
            case REGISTER_CAPTCHA_TYPE:
                return String.format("email:captcha:register:", uuid);
            case UPDATE_PASSWORD_CAPTCHA_TYPE:
                return String.format("email:captcha:update_password:", uuid);
            default:
                throw new IllegalArgumentException("captchaType is not support");
        }
    }

    public static MessageContent getEmailMessageContent(CaptchaType captchaType, String captcha, String email) {
        Map<String, Object> templateParams = Maps
                .of("code", captcha)
                .and("expireMinutes", "10")
                .build();
        switch (captchaType) {
            case LOGIN_CAPTCHA_TYPE:
                return MailMessage.of(email, "SMS_LOGIN_CODE", templateParams);
            case REGISTER_CAPTCHA_TYPE:
                return MailMessage.of(email, "SMS_REGISTER_CODE", templateParams);
            default:
                throw new IllegalArgumentException("captchaType is not support");
        }
    }

    public static MessageContent getSmsMessageContent(CaptchaType captchaType, String captcha, String phone) {
        switch (captchaType) {
            case UPDATE_PASSWORD_CAPTCHA_TYPE:
                Map<String, Object> templateParams = Maps
                        .of("code", captcha)
                        .and("expireMinutes", "10")
                        .build();
                return SmsMessage.of(phone, "SMS_LOGIN_CODE", templateParams);
            default:
                throw new IllegalArgumentException("captchaType is not support");
        }
    }
}
