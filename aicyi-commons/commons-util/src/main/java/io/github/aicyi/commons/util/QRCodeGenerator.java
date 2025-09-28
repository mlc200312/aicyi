package io.github.aicyi.commons.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 二维码生成器（建造者模式）
 * @date 2025/9/28
 **/
public class QRCodeGenerator {
    private String content;
    private int size = 300;
    private String format = "PNG";
    private Color frontColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private String logoPath;
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;
    private int margin = 1;

    public QRCodeGenerator(String content) {
        this.content = Objects.requireNonNull(content);
    }

    public QRCodeGenerator size(int size) {
        this.size = size;
        return this;
    }

    public QRCodeGenerator format(String format) {
        this.format = format;
        return this;
    }

    public QRCodeGenerator frontColor(Color color) {
        this.frontColor = color;
        return this;
    }

    public QRCodeGenerator backgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public QRCodeGenerator logo(String logoPath) {
        this.logoPath = logoPath;
        return this;
    }

    public QRCodeGenerator errorCorrection(ErrorCorrectionLevel level) {
        this.errorCorrectionLevel = level;
        return this;
    }

    public QRCodeGenerator margin(int margin) {
        this.margin = margin;
        return this;
    }

    public BufferedImage generateImage() throws IOException, WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, margin);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

        MatrixToImageConfig config = new MatrixToImageConfig(frontColor.getRGB(), backgroundColor.getRGB());
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

        if (logoPath != null) {
            addLogo(image, logoPath);
        }

        return image;
    }

    private void addLogo(BufferedImage qrImage, String logoPath) throws IOException {
        BufferedImage originalLogo = ImageIO.read(Paths.get(logoPath).toFile());
        int logoWidth = qrImage.getWidth() / 6;
        int logoHeight = qrImage.getHeight() / 6;
        // 计算居中位置
        int x = (qrImage.getWidth() - logoWidth) / 2;
        int y = (qrImage.getHeight() - logoHeight) / 2;

        // 创建高质量缩放的Logo图像
        BufferedImage scaledLogo = new BufferedImage(logoWidth, logoHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledLogo.createGraphics();

        // 开启抗锯齿和高质量渲染
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制缩放后的Logo
        g.drawImage(originalLogo, 0, 0, logoWidth, logoHeight, null);
        g.dispose();

        // 将Logo绘制到二维码上
        Graphics2D qrGraphics = qrImage.createGraphics();
        qrGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qrGraphics.drawImage(scaledLogo, x, y, null);
        qrGraphics.dispose();
    }

    public byte[] generateBytes() throws IOException, WriterException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage image = generateImage();
            ImageIO.write(image, format, outputStream);
            return outputStream.toByteArray();
        }
    }

    public void generateToFile(String filePath) throws IOException, WriterException {
        BufferedImage image = generateImage();
        ImageIO.write(image, format, Paths.get(filePath).toFile());
    }

    public void generateToStream(OutputStream outputStream) throws IOException, WriterException {
        BufferedImage image = generateImage();
        ImageIO.write(image, format, outputStream);
    }

    public void generateToResponse(HttpServletResponse response) throws IOException, WriterException {
        response.setContentType("image/" + format.toLowerCase());
        generateToStream(response.getOutputStream());
    }
}