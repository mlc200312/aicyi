package com.aichuangyi.commons.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author Mr.Min
 * @description 时间戳序列化
 * @date 2025/8/8
 **/
@JsonSerialize(using = TimestampJsonSerializer.class)
public class TimestampJsonSerializer extends JsonSerializer<Timestamp> {

    @Override
    public void serialize(Timestamp timestamp, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(timestamp.getTime());
    }
}