package io.github.aicyi.commons.util;

import io.github.aicyi.commons.core.BeanMapper;
import io.github.aicyi.commons.util.orikamapper.OrikaMapperRegistry;

/**
 * @author Mr.Min
 * @description Mapper 工具类
 * @date 2025/8/5
 **/
public class MapperUtils {

    private MapperUtils() {
    }

    public static BeanMapper getInstance() {
        return OrikaMapperRegistry.INSTANCE;
    }
}