package io.github.aicyi.commons.util.mapper;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Mr.Min
 * @description 构建字段映射
 * @date 2026/4/20
 **/
public class FieldMapBuilder {

    private final Map<String, String> fieldMap = new LinkedHashMap<>();
    private final Set<String> ignoreFields = new LinkedHashSet<>();

    private FieldMapBuilder() {
    }

    public static FieldMapBuilder create() {
        return new FieldMapBuilder();
    }

    /**
     * 添加字段
     */
    public FieldMapBuilder add(String fieldName, String fieldValue) {
        if (isValid(fieldName)) {
            fieldMap.put(fieldName, fieldValue);
        }
        return this;
    }

    /**
     * 添加忽略字段
     */
    public FieldMapBuilder ignore(String fieldName) {
        if (isValid(fieldName)) {
            ignoreFields.add(fieldName);
        }
        return this;
    }

    /**
     * 构建最终结果（自动剔除忽略字段）
     */
    public FieldBuildConfig build() {
        Map<String, String> finalMap = new LinkedHashMap<>(fieldMap);

        for (String ignoreField : ignoreFields) {
            finalMap.remove(ignoreField);
        }

        return new FieldBuildConfig(finalMap, new ArrayList<>(ignoreFields)
        );
    }

    /**
     * 字段合法性校验
     */
    private boolean isValid(String fieldName) {
        return StringUtils.isNotBlank(fieldName);
    }

    /**
     * @author Mr.Min
     * @description 字段构建结果
     * @date 2026/4/20
     **/
    public static class FieldBuildConfig {

        private final Map<String, String> fieldMap;
        private final List<String> ignoreFields;

        public FieldBuildConfig(Map<String, String> fieldMap, List<String> ignoreFields) {
            this.fieldMap = Collections.unmodifiableMap(fieldMap);
            this.ignoreFields = Collections.unmodifiableList(ignoreFields);
        }

        public Map<String, String> getFieldMap() {
            return fieldMap;
        }

        public List<String> getIgnoreFields() {
            return ignoreFields;
        }
    }
}