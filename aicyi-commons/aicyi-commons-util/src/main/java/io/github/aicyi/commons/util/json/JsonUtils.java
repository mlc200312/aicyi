package io.github.aicyi.commons.util.json;

import io.github.aicyi.commons.core.codec.JsonCodec;
import io.github.aicyi.commons.util.json.jackson.JacksonJsonCodec;

/**
 * @author Mr.Min
 * @description Json 工具类
 * @date 2025/8/5
 **/
public final class JsonUtils {

    private JsonUtils() {
    }

    /**
     * 获取全局默认 JsonCodec 实例
     */
    public static JsonCodec getInstance() {
        return JacksonJsonCodec.DEFAULT;
    }
}