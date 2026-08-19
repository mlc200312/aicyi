package io.github.aicyi.commons.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * JSON报文敏感信息脱敏
 * 相比正则：支持嵌套、数组、数字、布尔、null；不会被转义引号破坏结构
 */
public final class JsonSensitiveMaskUtils {

    private JsonSensitiveMaskUtils() {
    }

    /**
     * 复用全局编解码器，保持与 JacksonJsonCodec 配置一致
     */
    private static final ObjectMapper OBJECT_MAPPER = JacksonJsonCodec.DEFAULT.getObjectMapper();

    /**
     * 默认敏感词集合，key转小写后做包含匹配（如 access_token 命中 token）
     */
    public static final List<String> DEFAULT_SENSITIVE_WORDS = Collections.unmodifiableList(Arrays.asList(
            "password", "pwd", "passwd", "secret", "token", "credential", "authorization"
    ));

    /**
     * 对外入口：使用默认敏感词集合脱敏JSON字符串
     *
     * @param jsonBody 原始json字符串
     * @return 脱敏后的json字符串；非json/空直接原样返回
     */
    public static String maskJsonBody(String jsonBody) {
        return maskJsonBody(jsonBody, DEFAULT_SENSITIVE_WORDS);
    }

    /**
     * 对外入口：使用自定义敏感词集合脱敏JSON字符串
     *
     * @param jsonBody       原始json字符串
     * @param sensitiveWords 敏感词集合，null 或空时按默认集合处理
     * @return 脱敏后的json字符串；非json/空直接原样返回
     */
    public static String maskJsonBody(String jsonBody, Collection<String> sensitiveWords) {
        if (StringUtils.isBlank(jsonBody)) {
            return jsonBody;
        }
        Collection<String> words = (sensitiveWords == null || sensitiveWords.isEmpty())
                ? DEFAULT_SENSITIVE_WORDS : sensitiveWords;
        try {
            JsonNode root = OBJECT_MAPPER.readTree(jsonBody);
            maskNode(root, words);
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            // 解析失败，说明不是合法JSON，直接返回原文（不做脱敏，避免日志乱码）
            return jsonBody;
        }
    }

    /**
     * 递归遍历JsonNode，执行脱敏核心逻辑
     */
    private static void maskNode(JsonNode node, Collection<String> sensitiveWords) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode childNode = objectNode.get(fieldName);

                // key忽略大小写、包含匹配敏感词
                if (isSensitiveKey(fieldName, sensitiveWords)) {
                    // 不管原来是什么类型(string/number/boolean/null)，统一替换为掩码字符串
                    objectNode.put(fieldName, "******");
                } else {
                    // 非敏感字段，继续递归子节点
                    maskNode(childNode, sensitiveWords);
                }
            }
        } else if (node.isArray()) {
            // 处理数组，数组里面的每个元素递归脱敏
            ArrayNode arrayNode = (ArrayNode) node;
            for (JsonNode item : arrayNode) {
                maskNode(item, sensitiveWords);
            }
        }
        // 普通值节点(string/number/boolean/null)直接跳过，只有父object控制赋值
    }

    /**
     * 判断是否敏感key，忽略大小写，包含匹配；
     * 显式使用 ROOT Locale，避免土耳其语等特殊 Locale 下 I/i 转换异常
     */
    private static boolean isSensitiveKey(String fieldName, Collection<String> sensitiveWords) {
        String lowerKey = fieldName.toLowerCase(Locale.ROOT);
        for (String word : sensitiveWords) {
            if (lowerKey.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
