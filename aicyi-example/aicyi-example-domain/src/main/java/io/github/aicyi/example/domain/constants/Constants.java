package io.github.aicyi.example.domain.constants;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:07
 **/
public class Constants {

    public static String getCaptchaKey(String uuid) {
        return String.format("captcha:", uuid);
    }
}
