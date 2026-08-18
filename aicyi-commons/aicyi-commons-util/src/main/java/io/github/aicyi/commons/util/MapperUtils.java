package io.github.aicyi.commons.util;

import io.github.aicyi.commons.core.mapper.BeanMapper;
import io.github.aicyi.commons.util.orikamapper.OrikaMapperRegistry;

/**
 * @author Mr.Min
 * @description Mapper 工具类
 * @date 2025/8/5
 **/
public final class MapperUtils {

    private MapperUtils() {
    }

    /**
     * 获取全局默认 BeanMapper 实例（Orika 实现）
     */
    public static BeanMapper getInstance() {
        return OrikaMapperRegistry.INSTANCE;
    }
}