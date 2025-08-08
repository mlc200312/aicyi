package com.aichuangyi.base.util.json;

import com.aichuangyi.base.util.DateTimeUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Mr.Min
 * @description 业务描述
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
