package io.github.aicyi.commons.util;

import io.github.aicyi.commons.lang.SmartMapper;
import io.github.aicyi.commons.util.orikamapper.OrikaMapperRegistry;

/**
 * @author Mr.Min
 * @description Mapper 工具类
 * @date 2025/8/5
 **/
public class MapperUtils {

    private MapperUtils() {
    }

    public static SmartMapper getInstance() {
        return OrikaMapperRegistry.INSTANCE;
    }
}