package io.github.aicyi.commons.util;

import io.github.aicyi.commons.lang.SmartJsonMapper;
import io.github.aicyi.commons.util.jackson.JacksonJsonMapper;

/**
 * @author Mr.Min
 * @description Json 工具类
 * @date 2025/8/5
 **/
public class JsonUtils {

    private JsonUtils() {
    }

    private static class SingletonInner {
        private static final SmartJsonMapper INSTANCE = JacksonJsonMapper.DEFAULT;
    }

    public static SmartJsonMapper getInstance() {
        return SingletonInner.INSTANCE;
    }
}