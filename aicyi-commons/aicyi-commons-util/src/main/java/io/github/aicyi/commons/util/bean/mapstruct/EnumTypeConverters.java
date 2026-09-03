package io.github.aicyi.commons.util.bean.mapstruct;

import io.github.aicyi.commons.lang.EnumType;
import io.github.aicyi.commons.lang.StringEnumType;
import org.mapstruct.TargetType;

/**
 * 枚举 code 类型转换器（供各 MapStruct Mapper 通过 {@code uses} 引用）
 * <p>
 * 等价于原 Orika 的 EnumTypeMapperConverter / StringEnumTypeMapperConverter：
 * <ul>
 *     <li>{@link EnumType} 枚举与 Integer code 双向转换</li>
 *     <li>{@link StringEnumType} 枚举与 String code 双向转换</li>
 * </ul>
 * 使用方式：在 {@code @Mapper(uses = EnumTypeConverters.class)} 中引用，
 * MapStruct 通过无参构造实例化本类并自动匹配、调用这些转换方法。
 *
 * @author Mr.Min
 * @date 2026-09-02
 */
public class EnumTypeConverters {

    /**
     * 整型枚举 -&gt; code
     */
    public Integer toCode(EnumType value) {
        return value == null ? null : value.getCode();
    }

    /**
     * code -&gt; 整型枚举（按目标枚举类型逐项匹配 code）
     */
    public <T extends EnumType> T fromCode(Integer code, @TargetType Class<T> targetType) {
        if (code == null || targetType == null) {
            return null;
        }
        for (T constant : targetType.getEnumConstants()) {
            if (constant.getCode().equals(code)) {
                return constant;
            }
        }
        return null;
    }

    /**
     * 字符串枚举 -&gt; code
     */
    public String toCode(StringEnumType value) {
        return value == null ? null : value.getCode();
    }

    /**
     * code -&gt; 字符串枚举（按目标枚举类型逐项匹配 code）
     */
    public <T extends StringEnumType> T fromCode(String code, @TargetType Class<T> targetType) {
        if (code == null || targetType == null) {
            return null;
        }
        for (T constant : targetType.getEnumConstants()) {
            if (constant.getCode().equals(code)) {
                return constant;
            }
        }
        return null;
    }
}
