package com.aichuangyi.base.util.json;

import com.aichuangyi.base.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * @author Mr.Min
 * @description 时间类型序列化
 * @date 21:05
 **/
public class DateJsonSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Date> implements ContextualSerializer {
    private String pattern;

    public DateJsonSerializer() {
    }

    public DateJsonSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.isBlank(pattern)) {
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
