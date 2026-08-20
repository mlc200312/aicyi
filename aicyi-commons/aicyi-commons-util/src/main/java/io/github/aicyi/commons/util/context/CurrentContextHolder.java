package io.github.aicyi.commons.util.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 当前线程上下文
 * @date 2026/5/26
 **/
public final class CurrentContextHolder {

    public static final String CONTEXT_KEY_USER_ID = "currentUserId";
    public static final String CONTEXT_KEY_USERNAME = "currentUserName";

    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    private CurrentContextHolder() {
    }

    public static void set(String key, Object value) {
        Map<String, Object> map = CONTEXT.get();
        if (map == null) {
            map = new HashMap<>();
            CONTEXT.set(map);
        }
        map.put(key, value);
    }

    /**
     * 读取上下文值（只读，不产生初始化副作用；线程池场景请务必在请求结束时调用 remove）
     */
    public static Object get(String key) {
        Map<String, Object> map = CONTEXT.get();
        return map == null ? null : map.get(key);
    }

    private static String returnObjectValue(Object value) {
        return value == null ? null : value.toString();
    }

    public static void remove() {
        CONTEXT.remove();
    }

    /**
     * 移除指定 key，保留上下文中的其他条目
     */
    public static void remove(String key) {
        Map<String, Object> map = CONTEXT.get();
        if (map != null) {
            map.remove(key);
        }
    }

    public static String getUserId() {
        return returnObjectValue(get(CONTEXT_KEY_USER_ID));
    }

    public static String getUsername() {
        return returnObjectValue(get(CONTEXT_KEY_USERNAME));
    }

    public static void setUserId(String userID) {
        set(CONTEXT_KEY_USER_ID, userID);
    }

    public static void setUsername(String username) {
        set(CONTEXT_KEY_USERNAME, username);
    }
}
