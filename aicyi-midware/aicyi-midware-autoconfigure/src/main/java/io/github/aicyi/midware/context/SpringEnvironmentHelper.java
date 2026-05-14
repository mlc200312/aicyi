package io.github.aicyi.midware.context;

import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.util.JsonUtils;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description 环境配置工具类 用于便捷获取 Spring 环境配置信息，基于 Spring Environment 实现
 * @date 2026/4/29
 **/
public class SpringEnvironmentHelper implements InitializingBean {

    private static SpringEnvironmentHelper INSTANCE;

    private Environment environment;
    private JsonCodec jsonCodec;

    public SpringEnvironmentHelper(Environment environment) {
        this.environment = environment;
        this.jsonCodec = JsonUtils.getInstance();
    }

    public SpringEnvironmentHelper(Environment environment, JsonCodec jsonCodec) {
        this.environment = environment;
        this.jsonCodec = jsonCodec;
    }

    /**
     * 获取字符串
     */
    public static String getString(String key) {
        return INSTANCE.environment.getProperty(key);
    }

    public static String getString(String key, String defaultValue) {
        return INSTANCE.environment.getProperty(key, defaultValue);
    }

    /**
     * 获取指定类型
     */
    public static <T> T getProperty(String key, Class<T> targetType) {
        return INSTANCE.environment.getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return INSTANCE.environment.getProperty(key, targetType, defaultValue);
    }

    /**
     * 获取List
     * 配置格式:
     * app.white-list=a,b,c
     */
    public static List<String> getList(String key) {
        String value = getString(key);
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 获取Map
     * 配置格式:
     * app.config={"a":"1","b":"2"}
     */
    public static Map<String, Object> getMap(String key) {
        String json = getString(key);
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return INSTANCE.jsonCodec.fromJsonMap(json, String.class, Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse map property: " + key, e);
        }
    }

    /**
     * 获取对象
     * 配置格式:
     * app.user={"name":"Tom","age":18}
     */
    public static <T> T getObject(String key, Class<T> clazz) {
        String json = getString(key);
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return INSTANCE.jsonCodec.fromJson(json, JacksonTypeFactory.typeOf(clazz));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse object property: " + key, e);
        }
    }

    /**
     * 判断配置是否存在
     */
    public static boolean contains(String key) {
        return INSTANCE.environment.containsProperty(key);
    }

    /**
     * 当前激活环境
     */
    public static String[] getActiveProfiles() {
        return INSTANCE.environment.getActiveProfiles();
    }

    /**
     * 是否指定环境
     */
    public static boolean isActiveProfile(String profile) {
        return Arrays.asList(INSTANCE.environment.getActiveProfiles()).contains(profile);
    }

    /**
     * 是否生产环境
     */
    public static boolean isProd() {
        return isActiveProfile("prod");
    }

    /**
     * 是否测试环境
     */
    public static boolean isTest() {
        return isActiveProfile("test");
    }

    /**
     * 是否开发环境
     */
    public static boolean isDev() {
        return isActiveProfile("dev");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SpringEnvironmentHelper.INSTANCE = this;
        checkEnvironmentInitialized();
    }

    /**
     * 检查环境对象是否初始化，未初始化则抛出异常
     */
    private static void checkEnvironmentInitialized() {
        if (SpringEnvironmentHelper.INSTANCE.environment == null) {
            throw new IllegalStateException("EnvironmentUtils 未完成初始化，请确认 Spring 容器已加载且 Environment 已注入");
        }
    }
}