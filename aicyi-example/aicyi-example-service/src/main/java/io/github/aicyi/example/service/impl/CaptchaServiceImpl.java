package io.github.aicyi.example.service.impl;

import io.github.aicyi.commons.core.cache.StringCacheManager;
import io.github.aicyi.commons.core.context.SpringEnvironmentHelper;
import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.lang.BoBean;
import io.github.aicyi.commons.lang.exception.BusinessException;
import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.CaptchaUtils;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.example.domain.SendCaptchaParam;
import io.github.aicyi.example.domain.constants.Constants;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.example.domain.type.CaptchaType;
import io.github.aicyi.example.domain.type.ExampleResultCode;
import io.github.aicyi.example.service.CaptchaService;
import io.github.aicyi.example.service.UserService;
import io.github.aicyi.midware.message.core.model.MessageContent;
import io.github.aicyi.midware.message.core.sender.MessageSendCallback;
import io.github.aicyi.midware.message.core.model.MessageSendResult;
import io.github.aicyi.midware.message.core.sender.UnifiedMessageManager;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:38
 **/
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaService.class);

    @Autowired
    private UnifiedMessageManager unifiedMessageManager;
    @Autowired
    private StringCacheManager stringCacheManager;
    @Autowired
    private UserService userService;

    @Override
    public String saveCaptcha() {
        // 生成验证码
        String captcha = CaptchaUtils.randomCaptcha();
        String uuid = IdUtils.generateV7Id();

        // 缓存验证码
        String captchaKey = Constants.getCaptchaKey(uuid);
        stringCacheManager.put(captchaKey, captcha, Constants.CAPTCHA_EXPIRE, TimeUnit.MILLISECONDS);
        return uuid;
    }

    @Override
    public BufferedImage getCaptcha(String uuid) {
        String code = stringCacheManager.get(Constants.getCaptchaKey(uuid));
        return CaptchaUtils.generateImage(code);
    }

    @Override
    public String sendEmailCaptcha(SendCaptchaParam param) {
        return sendCaptcha(param, messageContentParam ->
                Constants.getEmailMessageContent(messageContentParam.getCaptchaType(), messageContentParam.getCaptcha(), messageContentParam.getEmail()));
    }

    @Override
    public String sendSmsCaptcha(SendCaptchaParam param) {
        return sendCaptcha(param, messageContentParam ->
                Constants.getSmsMessageContent(messageContentParam.getCaptchaType(), messageContentParam.getCaptcha(), messageContentParam.getMobile()));
    }

    @Override
    public void validateCaptcha(CaptchaType captchaType, String uuid, String captcha) {
        // 验证码缓存key
        String captchaKey = Constants.getCaptchaKey(captchaType, uuid);
        // 获取缓存验证码
        String code = stringCacheManager.get(captchaKey);
        boolean isEq = captcha.equalsIgnoreCase(code);
        // 测试环境不验证
        if (!SpringEnvironmentHelper.isProd()) {
            LOGGER.info("验证码校验：{}", isEq);
            return;
        }
        if (!isEq) {
            stringCacheManager.remove(Constants.getCaptchaKey(uuid));
            throw new BusinessException("验证码错误");
        }
    }

    private String sendCaptcha(SendCaptchaParam param, Function<MessageContentParam, MessageContent> function) {
        // 验证码校验
        validateCaptcha(CaptchaType.IMAGE_CAPTCHA_TYPE, param.getUuid(), param.getVerCode());

        // 查询用户信息
        User user = userService.getByUsername(param.getUsername());

        if (Objects.isNull(user)) {
            throw new BusinessException(ExampleResultCode.OBJECT_NOT_FOUND);
        }

        // 生成验证码
        CaptchaType captchaType = param.getCaptchaType();
        String captcha = CaptchaUtils.randomCaptcha();
        String uuid = IdUtils.generateV7Id();

        MessageContentParam messageContentParam = new MessageContentParam();
        messageContentParam.setCaptchaType(captchaType);
        messageContentParam.setCaptcha(captcha);
        messageContentParam.setEmail(user.getEmail());
        messageContentParam.setMobile(user.getMobile());

        MessageContent messageContent = function.apply(messageContentParam);

        unifiedMessageManager.sendAsync(messageContent, new MessageSendCallback() {
            @Override
            public void onComplete(MessageSendResult result) {

                // 缓存验证码
                String captchaKey = Constants.getCaptchaKey(captchaType, uuid);
                Long captchaExpire = Constants.getCaptchaExpire(captchaType);
                stringCacheManager.put(captchaKey, captcha, captchaExpire, TimeUnit.MILLISECONDS);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        return uuid;
    }


    @Getter
    @Setter
    static class MessageContentParam extends BaseBean implements BoBean {
        private CaptchaType captchaType;
        private String captcha;
        private String mobile;
        private String email;
    }
}
