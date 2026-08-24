package io.github.aicyi.midware.db.mybatisplus.handlers;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import io.github.aicyi.commons.util.json.JsonUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 11:51
 **/
@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringListTypeHandler extends AbstractJsonTypeHandler<List<String>> {


    @Override
    protected List<String> parse(String json) {
        return JsonUtils.getInstance().fromJsonList(json, String.class);
    }

    @Override
    protected String toJson(List<String> obj) {
        return JsonUtils.getInstance().toJson(obj);
    }
}
