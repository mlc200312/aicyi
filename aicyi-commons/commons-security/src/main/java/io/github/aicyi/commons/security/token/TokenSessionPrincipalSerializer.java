package io.github.aicyi.commons.security.token;


import com.fasterxml.jackson.databind.JavaType;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.token.PrincipalSerializer;
import io.github.aicyi.commons.util.JsonUtils;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;

/**
 * TokenSession Principal序列化器
 *
 * @param <P>
 */
public class TokenSessionPrincipalSerializer<P> implements PrincipalSerializer<TokenSession<P>> {

    private final JsonCodec jsonCodec;

    private final Class<? extends P> principalType;

    public TokenSessionPrincipalSerializer(JsonCodec jsonCodec, Class<? extends P> principalType) {

        this.jsonCodec = jsonCodec;
        this.principalType = principalType;
    }

    public TokenSessionPrincipalSerializer(Class<? extends P> principalType) {

        this.jsonCodec = JsonUtils.getInstance();
        this.principalType = principalType;
    }

    @Override
    public String serialize(TokenSession<P> session) {
        try {

            return jsonCodec.toJson(session);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public TokenSession<P> deserialize(String value) {

        try {

            JavaType javaType = JacksonTypeFactory.parametricTypeOf(TokenInfo.class, JacksonTypeFactory.typeOf(principalType));

            return jsonCodec.fromJson(value, javaType);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}