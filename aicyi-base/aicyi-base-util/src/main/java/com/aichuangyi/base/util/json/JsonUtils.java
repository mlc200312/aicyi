package com.aichuangyi.base.util.json;

import com.aichuangyi.base.core.JsonConverter;

/**
 * @author Mr.Min
 * @description Json 工具类
 * @date 2025/8/5
 **/
public class JsonUtils {

    private JsonUtils() {
    }

    private static class SingletonInner {
        private static final JsonConverter INSTANCE = new JacksonConverter();
    }

    public static JsonConverter getInstance() {
        return SingletonInner.INSTANCE;
    }
}