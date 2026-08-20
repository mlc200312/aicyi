package io.github.aicyi.commons.util.json.jackson;

import io.github.aicyi.commons.util.date.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.Date;

/**
 * @author Mr.Min
 * @description 时间类型序列化
 * @date 21:05
 **/
public class DateJsonSerializer extends JsonSerializer<Date> implements ContextualSerializer {
    private final String pattern;

    public DateJsonSerializer() {
        this.pattern = null;
    }

    public DateJsonSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (pattern == null || pattern.trim().isEmpty()) {
            jsonGenerator.writeString(DateUtils.formatDate(date));
        } else {
            jsonGenerator.writeString(DateUtils.formatDate(date, pattern));
        }

    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        String value = null;
        if (null != beanProperty) {
            JsonFormat annotation = beanProperty.getAnnotation(JsonFormat.class);
            if (null != annotation) {
                value = annotation.pattern();
            }
        }
        return new DateJsonSerializer(value);
    }
}
