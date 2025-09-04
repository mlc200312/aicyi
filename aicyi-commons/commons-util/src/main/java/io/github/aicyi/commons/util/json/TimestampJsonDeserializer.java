package io.github.aicyi.commons.util.json;

import io.github.aicyi.commons.util.DateTimeUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author Mr.Min
 * @description 时间戳类型反序列化
 * @date 21:06
 **/
public class TimestampJsonDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer {

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonToken jsonToken = jsonParser.currentToken();
        if (jsonToken == JsonToken.VALUE_STRING) {
            return Timestamp.valueOf(DateTimeUtils.toLDateTime(jsonParser.getText()));
        } else if (jsonToken.isNumeric()) {
            return new Timestamp(jsonParser.getValueAsLong());
        }
        return null;
    }
}
