package io.github.aicyi.midware.db.mybatisplus.handlers;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import io.github.aicyi.commons.util.json.JsonUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Mr.Min
 * @description List&lt;String&gt; 与 JSON 字符串互转的 TypeHandler
 * <p>
 * MyBatis-Plus 3.5.7 起 AbstractJsonTypeHandler 移除了无参构造器（改为 (Class) / (Class, Field)），
 * 且 parse / toJson 由 protected 抽象方法提升为 IJsonTypeHandler 的 public 接口方法，
 * 此处按官方 JacksonTypeHandler 的范式适配。
 * @date 11:51
 **/
@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringListTypeHandler extends AbstractJsonTypeHandler<List<String>> {

    public StringListTypeHandler(Class<?> type) {
        super(type);
    }

    public StringListTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public List<String> parse(String json) {
        return JsonUtils.getInstance().fromJsonList(json, String.class);
    }

    @Override
    public String toJson(List<String> obj) {
        return JsonUtils.getInstance().toJson(obj);
    }
}
