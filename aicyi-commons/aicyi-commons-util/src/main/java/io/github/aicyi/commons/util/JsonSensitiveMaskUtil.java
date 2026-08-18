package io.github.aicyi.commons.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * JSON报文敏感信息脱敏
 * 相比正则：支持嵌套、数组、数字、布尔、null；不会被转义引号破坏结构
 */
public final class JsonSensitiveMaskUtil {

    private JsonSensitiveMaskUtil() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 敏感词集合，key转小写后做包含匹配（如 access_token 命中 token），可按需扩展
     */
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "password", "pwd", "passwd", "secret", "token", "credential", "authorization"
    );

    /**
     * 对外入口：脱敏JSON字符串
     *
     * @param jsonBody 原始json字符串
     * @return 脱敏后的json字符串；非json/空直接原样返回
     */
    public static String maskJsonBody(String jsonBody) {
        if (StringUtils.isBlank(jsonBody)) {
            return jsonBody;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(jsonBody);
            maskNode(root);
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            // 解析失败，说明不是合法JSON，直接返回原文（不做脱敏，避免日志乱码）
            return jsonBody;
        }
    }

    /**
     * 递归遍历JsonNode，执行脱敏核心逻辑
     */
    private static void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode childNode = objectNode.get(fieldName);

                // key忽略大小写、包含匹配敏感词
                if (isSensitiveKey(fieldName)) {
                    // 不管原来是什么类型(string/number/boolean/null)，统一替换为掩码字符串
                    objectNode.put(fieldName, "******");
                } else {
                    // 非敏感字段，继续递归子节点
                    maskNode(childNode);
                }
            }
        } else if (node.isArray()) {
            // 处理数组，数组里面的每个元素递归脱敏
            ArrayNode arrayNode = (ArrayNode) node;
            for (JsonNode item : arrayNode) {
                maskNode(item);
            }
        }
        // 普通值节点(string/number/boolean/null)直接跳过，只有父object控制赋值
    }

    /**
     * 判断是否敏感key，忽略大小写，包含匹配
     */
    private static boolean isSensitiveKey(String fieldName) {
        String lowerKey = fieldName.toLowerCase();
        for (String word : SENSITIVE_WORDS) {
            if (lowerKey.contains(word)) {
                return true;
            }
        }
        return false;
    }
}