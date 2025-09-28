package io.github.aicyi.test.commons.util;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.github.aicyi.commons.util.QRCodeGenerator;
import io.github.aicyi.test.util.BaseLoggerTest;
import lombok.SneakyThrows;
import org.junit.Test;

import java.awt.*;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:17
 **/
public class QRCodeGeneratorTest extends BaseLoggerTest {

    @Test
    @SneakyThrows
    @Override
    public void test() {

        new QRCodeGenerator("https://www.baidu.com")
                .size(1000)
                .frontColor(Color.black)
                .backgroundColor(Color.WHITE)
                .logo("src/test/resources/test/logo/aicyi_logo.png")
                .errorCorrection(ErrorCorrectionLevel.H)
                .generateToFile("color_qrcode.png");
    }
}
