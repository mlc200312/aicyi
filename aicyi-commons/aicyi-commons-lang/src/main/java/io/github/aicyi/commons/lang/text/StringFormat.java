package io.github.aicyi.commons.lang.text;

import java.util.Map;
import java.util.Objects;

/**
 * 字符串格式化工具类
 *
 * <p>支持：
 * <ul>
 *     <li>顺序占位符：{}</li>
 *     <li>命名占位符：${name}</li>
 *     <li>转义字符：\{}、\${}</li>
 * </ul>
 *
 * <pre>
 * 示例：
 *
 * StringFormat.format("Hello {}, age {}", "Tom", 18);
 * -> Hello Tom, age 18
 *
 * Map<String, Object> params = new HashMap<>();
 * params.put("name", "Tom");
 * params.put("age", 18);
 * StringFormat.formatNamed("Hello ${name}, age ${age}", params);
 * -> Hello Tom, age 18
 *
 * StringFormat.escape:
 * "\\{}" -> "{}"
 * "\\${name}" -> "${name}"
 * </pre>
 *
 * @author Mr.Min
 */
public final class StringFormat {

    /**
     * {}
     */
    private static final String DEFAULT_OPEN_TOKEN = "{";

    /**
     * {}
     */
    private static final String DEFAULT_CLOSE_TOKEN = "}";

    /**
     * ${}
     */
    private static final String NAMED_OPEN_TOKEN = "${";

    private StringFormat() {
    }

    // =========================================================================
    // 顺序参数格式化
    // =========================================================================

    /**
     * 使用 {} 占位符格式化字符串
     *
     * <pre>
     * format("Hello {}", "Tom")
     * -> Hello Tom
     * </pre>
     */
    public static String format(String text, Object... args) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        if (args == null || args.length == 0) {
            return text;
        }

        return parse(
                text,
                DEFAULT_OPEN_TOKEN,
                DEFAULT_CLOSE_TOKEN,
                expression -> {
                    int index = expression.index;

                    if (index >= args.length) {
                        // 参数不足：保留原占位符
                        return expression.raw;
                    }

                    Object value = args[index];

                    return Objects.toString(value, "");
                }
        );
    }

    // =========================================================================
    // 命名参数格式化
    // =========================================================================

    /**
     * 使用 ${name} 命名占位符格式化
     *
     * <pre>
     * formatNamed(
     *      "Hello ${name}",
     *      Collections.singletonMap("name", "Tom")
     * )
     * -> Hello Tom
     * </pre>
     */
    public static String formatNamed(String text, Map<String, ?> params) {

        if (text == null || text.isEmpty()) {
            return text;
        }

        if (params == null || params.isEmpty()) {
            return text;
        }

        return parse(
                text,
                NAMED_OPEN_TOKEN,
                DEFAULT_CLOSE_TOKEN,
                expression -> {
                    Object value = params.get(expression.content);

                    // 未匹配参数：保留原 token
                    if (value == null && !params.containsKey(expression.content)) {
                        return expression.raw;
                    }

                    return Objects.toString(value, "");
                }
        );
    }

    // =========================================================================
    // 核心解析器
    // =========================================================================

    /**
     * 核心 token parser
     */
    private static String parse(String text, String openToken, String closeToken, TokenHandler handler) {

        validateToken(openToken, closeToken);

        int start = text.indexOf(openToken);

        if (start == -1) {
            return text;
        }

        char[] src = text.toCharArray();

        int offset = 0;

        int argIndex = 0;

        StringBuilder builder = new StringBuilder(text.length() + 32);

        while (start > -1) {

            // 判断是否被转义
            if (isEscaped(src, start)) {

                // 删除转义符 '\'
                builder.append(src, offset, start - offset - 1);

                // 添加 token 本体
                builder.append(openToken);

                offset = start + openToken.length();
            } else {

                int end = text.indexOf(closeToken,
                        start + openToken.length());

                // token 未闭合
                if (end == -1) {

                    builder.append(src, offset, src.length - offset);

                    offset = src.length;

                    break;
                }

                // 添加 token 前文本
                builder.append(src, offset, start - offset);

                // token 内容
                String content = text.substring(
                        start + openToken.length(),
                        end
                );

                String raw = text.substring(
                        start,
                        end + closeToken.length()
                );

                TokenExpression expression = new TokenExpression(
                        raw,
                        content,
                        argIndex++
                );

                String value = handler.handle(expression);

                builder.append(value);

                offset = end + closeToken.length();
            }

            start = text.indexOf(openToken, offset);
        }

        // 添加尾部内容
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }

        return builder.toString();
    }

    // =========================================================================
    // 工具方法
    // =========================================================================

    /**
     * 是否被转义
     *
     * <pre>
     * \{}
     * \${}
     * </pre>
     */
    private static boolean isEscaped(char[] src, int index) {

        if (index == 0) {
            return false;
        }

        return src[index - 1] == '\\';
    }

    /**
     * token 校验
     */
    private static void validateToken(String openToken, String closeToken) {

        if (openToken == null || openToken.isEmpty()) {
            throw new IllegalArgumentException("openToken must not be empty");
        }

        if (closeToken == null || closeToken.isEmpty()) {
            throw new IllegalArgumentException("closeToken must not be empty");
        }
    }

    // =========================================================================
    // 内部接口
    // =========================================================================

    @FunctionalInterface
    private interface TokenHandler {

        String handle(TokenExpression expression);
    }

    // =========================================================================
    // Token表达式
    // =========================================================================

    /**
     * token 信息
     */
    private static final class TokenExpression {

        /**
         * 原始 token
         * 例如：
         * ${name}
         */
        private final String raw;

        /**
         * token 内容
         * 例如：
         * name
         */
        private final String content;

        /**
         * 参数索引
         */
        private final int index;

        private TokenExpression(String raw, String content, int index) {

            this.raw = raw;
            this.content = content;
            this.index = index;
        }
    }
}