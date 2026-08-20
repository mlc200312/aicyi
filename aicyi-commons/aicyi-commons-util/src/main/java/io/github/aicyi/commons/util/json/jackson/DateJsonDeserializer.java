package io.github.aicyi.commons.util.json.jackson;

import io.github.aicyi.commons.util.date.DateUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * @author Mr.Min
 * @description 时间类型反序列化
 * @date 21:06
 **/
public class DateJsonDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return DateUtils.parseDate(jsonParser.getText());
    }
}
