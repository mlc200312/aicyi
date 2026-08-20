package io.github.aicyi.commons.util.json.jackson;

import io.github.aicyi.commons.util.date.DateTimeUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Mr.Min
 * @description 时间戳类型反序列化
 * @date 21:06
 **/
public class TimestampJsonDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonToken jsonToken = jsonParser.currentToken();
        if (jsonToken == JsonToken.VALUE_STRING) {
            String text = jsonParser.getText();
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            LocalDateTime parsed = DateTimeUtils.parseAuto(text);
            if (parsed == null) {
                throw new IllegalArgumentException("can not parse timestamp from: " + text);
            }
            return Timestamp.valueOf(parsed);
        } else if (jsonToken.isNumeric()) {
            return new Timestamp(jsonParser.getValueAsLong());
        }
        return null;
    }
}
