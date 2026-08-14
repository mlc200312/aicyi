package io.github.aicyi.midware.web.util;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Mr.Min
 * @description 字符集工具
 * @date 2026/8/14
 **/
public final class CharsetUtils {

    private CharsetUtils() {
    }

    /**
     * 解析字符集，非法或未指定时回退 UTF-8
     *
     * @param encoding 字符集名称
     * @return 字符集
     */
    public static Charset resolveCharset(String encoding) {
        if (StringUtils.isBlank(encoding)) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }
}
