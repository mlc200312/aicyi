package io.github.aicyi.commons.util.codec;


import io.github.aicyi.commons.core.codec.JsonCodec;
import io.github.aicyi.commons.core.PrincipalSerializer;
import io.github.aicyi.commons.util.json.JsonUtils;

/**
 * Jackson JWT Principal序列化器
 *
 * @param <P>
 */
public class JsonCodecPrincipalSerializer<P> implements PrincipalSerializer<P> {

    private final JsonCodec jsonCodec;

    private final Class<? extends P> principalType;

    public JsonCodecPrincipalSerializer(JsonCodec jsonCodec, Class<? extends P> principalType) {

        this.jsonCodec = jsonCodec;
        this.principalType = principalType;
    }

    public JsonCodecPrincipalSerializer(Class<? extends P> principalType) {

        this.jsonCodec = JsonUtils.getInstance();
        this.principalType = principalType;
    }

    @Override
    public String serialize(P principal) {
        return jsonCodec.toJson(principal);
    }

    @Override
    public P deserialize(String value) {
        return jsonCodec.fromJson(value, principalType);
    }
}