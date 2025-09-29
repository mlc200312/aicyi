package io.github.aicyi.commons.util;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author Mr.Min
 * @description 验证码工具类
 * @date 2025/9/29
 **/
public class CaptchaUtils {
    // 验证码字符集
    private static final String CHAR_SET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private static final int DEFAULT_WIDTH = 150;
    private static final int DEFAULT_HEIGHT = 50;
    private static final int DEFAULT_LENGTH = 4;
    private static final Font[] FONTS = {
            new Font("Arial", Font.BOLD, 28),
            new Font("Times New Roman", Font.ITALIC, 28),
            new Font("Courier New", Font.BOLD, 28)
    };

    /**
     * 生成随机验证码
     *
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }
        return sb.toString();
    }

    /**
     * 生成默认长度验证码
     *
     * @return 验证码字符串
     */
    public static String generateCode() {
        return generateCode(DEFAULT_LENGTH);
    }

    /**
     * 生成验证码图片
     *
     * @param code   验证码字符串
     * @param width  图片宽度
     * @param height 图片高度
     * @return BufferedImage对象
     */
    public static BufferedImage generateImage(String code, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, getRandomLightColor(), width, height, getRandomLightColor());
        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);

        // 添加噪点
        addNoise(g, width, height);

        // 绘制扭曲的字符
        drawDistortedText(g, code, width, height);

        // 添加干扰线
        addInterferenceLines(g, width, height);

        g.dispose();
        return image;
    }

    /**
     * 生成默认尺寸验证码图片
     *
     * @param code 验证码字符串
     * @return BufferedImage对象
     */
    public static BufferedImage generateImage(String code) {
        return generateImage(code, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 将验证码图片写入输出流
     *
     * @param image        验证码图片
     * @param outputStream 输出流
     * @param format       图片格式
     * @throws IOException
     */
    public static void writeToStream(BufferedImage image, OutputStream outputStream, String format) throws IOException {
        ImageIO.write(image, format, outputStream);
    }

    /**
     * 将验证码图片转换为字节数组
     *
     * @param image  验证码图片
     * @param format 图片格式
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] toByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 生成验证码并输出到响应流
     *
     * @param response HttpServletResponse
     * @throws IOException
     */
    public static void outputToResponse(HttpServletResponse response) throws IOException {
        String code = generateCode();
        BufferedImage image = generateImage(code);

        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        writeToStream(image, response.getOutputStream(), "jpeg");
    }

    /**
     * 添加噪点
     *
     * @param g
     * @param width
     * @param height
     */
    private static void addNoise(Graphics2D g, int width, int height) {
        Random random = new Random();
        int noiseCount = width * height / 30; // 计算噪点数量

        // 创建噪点图像
        BufferedImage noiseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < noiseCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = getRandomColor().getRGB();
            noiseImage.setRGB(x, y, rgb);
        }

        // 将噪点图像绘制到主图像
        g.drawImage(noiseImage, 0, 0, null);
    }

    /**
     * 绘制扭曲的文本
     *
     * @param g
     * @param code
     * @param width
     * @param height
     */
    private static void drawDistortedText(Graphics2D g, String code, int width, int height) {
        Random random = new Random();
        int charWidth = width / code.length();

        for (int i = 0; i < code.length(); i++) {
            // 随机选择字体
            g.setFont(getRandomFont());

            // 随机颜色
            g.setColor(getRandomDarkColor());

            // 字符位置
            int x = charWidth * i + charWidth / 4;
            int y = height / 2 + random.nextInt(height / 4);

            // 创建扭曲变换
            AffineTransform transform = new AffineTransform();
            double shearX = random.nextDouble() * 0.3 - 0.15;
            double shearY = random.nextDouble() * 0.3 - 0.15;
            transform.shear(shearX, shearY);
            transform.rotate(random.nextDouble() * 0.3 - 0.15, x, y);
            g.setTransform(transform);

            // 绘制字符
            g.drawString(String.valueOf(code.charAt(i)), x, y);

            // 重置变换
            g.setTransform(new AffineTransform());
        }
    }

    /**
     * 添加干扰线
     *
     * @param g
     * @param width
     * @param height
     */
    private static void addInterferenceLines(Graphics2D g, int width, int height) {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(width / 2);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width / 2) + width / 2;
            int y2 = random.nextInt(height);

            g.setColor(getRandomColor());
            g.setStroke(new BasicStroke(1 + random.nextFloat()));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 生成随机字体
     *
     * @return
     */
    private static Font getRandomFont() {
        Random random = new Random();
        return FONTS[random.nextInt(FONTS.length)];
    }

    /**
     * 生成随机颜色
     *
     * @return
     */
    private static Color getRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 获取随机光色
     *
     * @return
     */
    private static Color getRandomLightColor() {
        Random random = new Random();
        return new Color(200 + random.nextInt(56),
                200 + random.nextInt(56),
                200 + random.nextInt(56));
    }

    /**
     * 获取随机深色
     *
     * @return
     */
    private static Color getRandomDarkColor() {
        Random random = new Random();
        return new Color(random.nextInt(150),
                random.nextInt(150),
                random.nextInt(150));
    }
}
