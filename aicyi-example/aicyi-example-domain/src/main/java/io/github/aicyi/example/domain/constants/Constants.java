package io.github.aicyi.example.domain.constants;

import io.github.aicyi.commons.core.message.MessageContent;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.example.domain.type.CaptchaType;
import io.github.aicyi.midware.message.mail.MailMessage;
import io.github.aicyi.midware.message.sms.SmsMessage;

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
        switch (captchaType) {
            case LOGIN_CAPTCHA_TYPE:
                return MailMessage.of(String.format("【爱创意】验证码：%s，（10分钟内有效）。您正在登录爱创意Aicyi，请勿将验证码告诉他人哦。", captcha), "【爱创意】登录账号", email);
            case REGISTER_CAPTCHA_TYPE:
                return MailMessage.of(String.format("【爱创意】验证码：%s，（10分钟内有效）。您正在注册爱创意Aicyi，请勿将验证码告诉他人哦。", captcha), "【爱创意】注册账号", email);
            case UPDATE_PASSWORD_CAPTCHA_TYPE:
                return MailMessage.of(String.format("【爱创意】验证码：%s，（10分钟内有效）。您正在修改密码，请勿将验证码告诉他人哦。", captcha), "【爱创意】修改密码", email);
            default:
                throw new IllegalArgumentException("captchaType is not support");
        }
    }

    public static MessageContent getSmsMessageContent(CaptchaType captchaType, String captcha, String phone) {
        switch (captchaType) {
            case UPDATE_PASSWORD_CAPTCHA_TYPE:
                Map<String, String> templateParams = Maps
                        .of("captcha", captcha)
                        .build();
                return SmsMessage.of("1", "+86" + phone, templateParams, "Test");
            default:
                throw new IllegalArgumentException("captchaType is not support");
        }
    }
}
