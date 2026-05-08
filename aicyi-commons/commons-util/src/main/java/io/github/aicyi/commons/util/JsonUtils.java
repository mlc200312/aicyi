package io.github.aicyi.commons.util;

import io.github.aicyi.commons.lang.JsonCodec;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;

/**
 * @author Mr.Min
 * @description Json 工具类
 * @date 2025/8/5
 **/
public class JsonUtils {

    private JsonUtils() {
    }

    private static class SingletonInner {
        private static final JsonCodec INSTANCE = JacksonJsonCodec.DEFAULT;
    }

    public static JsonCodec getInstance() {
        return SingletonInner.INSTANCE;
    }
}