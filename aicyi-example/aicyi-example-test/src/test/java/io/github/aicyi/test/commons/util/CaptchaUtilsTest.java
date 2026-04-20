package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.util.CaptchaUtils;
import io.github.aicyi.test.util.BaseLoggerTest;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 2025/9/29
 **/
public class CaptchaUtilsTest extends BaseLoggerTest {

    @Test
    @Override
    public void test() {
        try {
            // 生成验证码
            String code = CaptchaUtils.generateCode(4);
            BufferedImage image = CaptchaUtils.generateImage(code);
            ImageIO.write(image, "png", Paths.get("captcha.png").toFile());
            System.out.println("验证码生成成功: " + code);

            BufferedImage image2 = CaptchaUtils.generateImage("2 + 4 = ?");
            ImageIO.write(image2, "png", Paths.get("captcha2.png").toFile());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}