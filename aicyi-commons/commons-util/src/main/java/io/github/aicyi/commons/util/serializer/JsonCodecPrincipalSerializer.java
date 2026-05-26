package io.github.aicyi.commons.util.serializer;


import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.PrincipalSerializer;
import io.github.aicyi.commons.util.JsonUtils;

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

        try {

            return jsonCodec.toJson(principal);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public P deserialize(String value) {

        try {

            return jsonCodec.fromJson(value, principalType);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}