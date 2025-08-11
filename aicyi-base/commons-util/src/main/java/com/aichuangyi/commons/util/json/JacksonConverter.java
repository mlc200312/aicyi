package com.aichuangyi.commons.util.json;

import com.aichuangyi.commons.EnumType;
import com.aichuangyi.commons.JsonConverter;
import com.aichuangyi.commons.StringEnumType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Mr.Min
 * @description Json 序列化
 * @date 2025/8/5
 **/
public class JacksonConverter extends ObjectMapper implements JsonConverter {

    public JacksonConverter() {
        // Include.Include.ALWAYS 默认
        // Include.NON_DEFAULT 属性为默认值不序列化
        // Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化，则返回的json是没有这个字段的。这样对移动端会更省流量
        // Include.NON_NULL 属性为NULL 不序列化
        this(JsonInclude.Include.NON_NULL);
    }

    /**
     * 通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
     *
     * @param include
     */
    public JacksonConverter(JsonInclude.Include include) {
        //设置输出时包含属性的风格
        if (include != null) {
            this.setSerializationInclusion(include);
        }
        //设置时区
        this.setTimeZone(TimeZone.getDefault());
        //初始化
        this.initModule();
    }

    /**
     * 初始化
     */
    private void initModule() {
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.registerModule(
                        new JavaTimeModule()
                                .addDeserializer(Timestamp.class, new TimestampJsonDeserializer())
                                .addDeserializer(Date.class, new DateJsonDeserializer())
                                .addSerializer(Timestamp.class, new TimestampJsonSerializer())
                                .addSerializer(Date.class, new DateJsonSerializer())
                )
                .registerModule(
                        new SimpleModule()
                                .addSerializer(EnumType.class, new EnumTypeJsonSerializer())
                                .addSerializer(StringEnumType.class, new StringEnumTypeJsonSerializer())
                );
    }

    /**
     * 简单设置
     *
     * @return
     */
    public final JacksonConverter enableSimple() {
        //解析器支持解析单引号
        this.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        //解析器支持解析结束符
        this.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        //允许出现特殊字符和转义符
//        this.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性,在遇到未知属性的时候不抛出异常
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return this;
    }

    @Override
    public <T extends Serializable> JavaType constructType(Class<T> clazz) {
        return this.getTypeFactory().constructType(clazz);
    }

    @Override
    public JavaType constructParametricType(Class<?> clazz, Type... parameterTypes) {
        JavaType[] array = Arrays.stream(parameterTypes).map(item -> this.getTypeFactory().constructType(item)).toArray(value -> new JavaType[value]);
        return this.getTypeFactory().constructParametricType(clazz, array);
    }

    @Override
    public String toJson(Object object) {
        if (null == object) {
            return null;
        }
        try {
            return this.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * json转对象
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public <T> T parse(String json, JavaType type) {
        try {
            return this.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T parse(String json, Type type) {
        return this.parse(json, this.constructType(type));
    }
}
