package com.aichuangyi.base.util.json;

import com.aichuangyi.base.util.DateUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 21:06
 **/
public class DateJsonDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer {

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return DateUtils.parseDate(jsonParser.getText());
    }
}
