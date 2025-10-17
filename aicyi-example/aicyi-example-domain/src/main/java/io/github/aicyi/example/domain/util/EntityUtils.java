package io.github.aicyi.example.domain.util;

import io.github.aicyi.commons.lang.BaseEntity;
import io.github.aicyi.commons.lang.type.BooleanType;
import io.github.aicyi.commons.util.ReflectionUtils;
import io.github.aicyi.commons.util.id.IdGenerator;

import java.time.LocalDateTime;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:18
 **/
public class EntityUtils {

    public static void setDefaultValue(BaseEntity baseEntity) {
        setDefaultValues(baseEntity, new String[]{"id", "deleted", "version", "createTime", "updateTime"}, new Object[]{IdGenerator.generateId(), BooleanType.FALSE, 0, LocalDateTime.now(), LocalDateTime.now()});
    }

    private static <T> void setDefaultValues(T entity, String[] fields, Object[] value) {
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            if (ReflectionUtils.hasField(entity, field)) {
                ReflectionUtils.invokeSetter(entity, field, value[i]);
            }
        }
    }
}
