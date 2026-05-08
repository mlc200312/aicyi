package io.github.aicyi.commons.util.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.aicyi.commons.lang.EnumType;
import io.github.aicyi.commons.lang.JsonCodec;
import io.github.aicyi.commons.lang.StringEnumType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Mr.Min
 * @description 默认的JSON映射器
 * @date 2019-05-22
 */
public final class JacksonJsonCodec implements JsonCodec {

    public static final JacksonJsonCodec DEFAULT = new JacksonJsonCodec().enableLenientMode();

    private final ObjectMapper objectMapper;

    public JacksonJsonCodec() {
        this(JsonInclude.Include.NON_NULL);
    }

    public JacksonJsonCodec(JsonInclude.Include include) {
        this.objectMapper = new ObjectMapper();

        objectMapper.setSerializationInclusion(include);
        objectMapper.setTimeZone(TimeZone.getDefault());

        registerModules();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private void registerModules() {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.registerModule(
                new JavaTimeModule()
                        .addDeserializer(Timestamp.class, new TimestampJsonDeserializer())
                        .addDeserializer(Date.class, new DateJsonDeserializer())
                        .addSerializer(Timestamp.class, new TimestampJsonSerializer())
                        .addSerializer(Date.class, new DateJsonSerializer())
        );

        objectMapper.registerModule(
                new SimpleModule()
                        .addSerializer(EnumType.class, new EnumTypeJsonSerializer())
                        .addSerializer(StringEnumType.class, new StringEnumTypeJsonSerializer())
        );
    }

    public JacksonJsonCodec enableLenientMode() {
        objectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return this;
    }

    @Override
    public JavaType createType(Class<?> type) {
        return objectMapper.getTypeFactory().constructType(type);
    }

    @Override
    public JavaType createParameterizedType(Class<?> rawType, Type... parameterTypes) {
        JavaType[] javaTypes = Arrays.stream(parameterTypes)
                .map(type -> objectMapper.getTypeFactory().constructType(type))
                .toArray(JavaType[]::new);

        return objectMapper.getTypeFactory().constructParametricType(rawType, javaTypes);
    }

    @Override
    public String toJson(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Object serialization failed.", e);
        }
    }

    public String toPrettyJson(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Object serialization failed.", e);
        }
    }

    public <T> T fromJson(String json, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON deserialization failed.", e);
        }
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        return fromJson(json, objectMapper.getTypeFactory().constructType(type));
    }
}