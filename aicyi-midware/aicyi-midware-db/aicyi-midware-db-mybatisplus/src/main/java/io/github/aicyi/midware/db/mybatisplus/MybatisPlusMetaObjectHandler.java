package io.github.aicyi.midware.db.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @author Mr.Min
 * @description MyBatis-Plus 公共字段自动填充处理器
 * <p>
 * 与 {@code aicyi-commons-lang} 的 BaseEntity 约定字段名对齐（createTime / updateTime / deleted / version）：
 * 插入时填充创建/更新时间并给 deleted、version 初始值，更新时仅刷新 updateTime。
 * 仅对带 {@code @TableField(fill = ...)} 标注的字段生效（strict 填充按字段元数据校验类型）。
 * @date 2026/8/20
 **/
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    private static final String FIELD_CREATE_TIME = "createTime";
    private static final String FIELD_UPDATE_TIME = "updateTime";
    private static final String FIELD_DELETED = "deleted";
    private static final String FIELD_VERSION = "version";

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, FIELD_CREATE_TIME, LocalDateTime.class, now);
        strictInsertFill(metaObject, FIELD_UPDATE_TIME, LocalDateTime.class, now);
        fillDeleted(metaObject);
        strictInsertFill(metaObject, FIELD_VERSION, Integer.class, 0);
    }

    /**
     * 按实体 deleted 字段的实际类型填充初始值：Boolean -> false，其余按 Integer 0。
     * strictInsertFill 要求填充类型与字段类型完全一致，否则会静默跳过，故此处区分处理。
     */
    private void fillDeleted(MetaObject metaObject) {
        if (!metaObject.hasGetter(FIELD_DELETED)) {
            return;
        }
        Class<?> deletedType = metaObject.getGetterType(FIELD_DELETED);
        if (boolean.class.equals(deletedType) || Boolean.class.equals(deletedType)) {
            strictInsertFill(metaObject, FIELD_DELETED, Boolean.class, Boolean.FALSE);
        } else {
            strictInsertFill(metaObject, FIELD_DELETED, Integer.class, 0);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, FIELD_UPDATE_TIME, LocalDateTime.class, LocalDateTime.now());
    }
}
