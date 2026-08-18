package io.github.aicyi.midware.redis;

import io.github.aicyi.commons.core.codec.JsonCodec;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonCodecRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET;
    private final Type type;
    private JsonCodec jsonCodec = JacksonJsonCodec.DEFAULT;

    public JsonCodecRedisSerializer(Class<T> type) {
        this.type = this.getType(type);
    }

    public JsonCodecRedisSerializer(Type type) {
        this.type = type;
    }

    public T deserialize(@Nullable byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        } else {
            try {
                return this.jsonCodec.fromJson(new String(bytes, DEFAULT_CHARSET), this.type);
            } catch (Exception var3) {
                Exception ex = var3;
                throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
            }
        }
    }

    public byte[] serialize(@Nullable Object t) throws SerializationException {
        if (t == null) {
            return JsonCodec.EMPTY_ARRAY;
        } else {
            try {
                return this.jsonCodec.toJson(t).getBytes(DEFAULT_CHARSET);
            } catch (Exception var3) {
                Exception ex = var3;
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        }
    }

    public void setJsonCodec(JsonCodec jsonCodec) {
        Assert.notNull(jsonCodec, "jsonCodec");
        this.jsonCodec = jsonCodec;
    }

    protected Type getType(Class<?> clazz) {
        return jsonCodec.createType(clazz);
    }

    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
    }
}