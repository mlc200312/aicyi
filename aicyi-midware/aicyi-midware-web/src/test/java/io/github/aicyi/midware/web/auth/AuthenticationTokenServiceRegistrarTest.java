package io.github.aicyi.midware.web.auth;

import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import io.github.aicyi.commons.core.token.AuthenticationTokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link AuthenticationTokenServiceRegistrar} 单元测试
 */
class AuthenticationTokenServiceRegistrarTest {

    @SuppressWarnings("unchecked")
    private final ObjectProvider<AuthenticationTokenService<?>> provider = mock(ObjectProvider.class);

    @Test
    void startupFailsWhenNoTokenService() {
        when(provider.stream()).thenReturn(Stream.empty());

        assertThrows(IllegalStateException.class,
                () -> new AuthenticationTokenServiceRegistrar(provider).afterPropertiesSet());
    }

    @SuppressWarnings("unchecked")
    @Test
    void registersFirstTokenService() throws Exception {
        AuthenticationTokenService<?> tokenService = mock(AuthenticationTokenService.class);
        when(provider.stream()).thenReturn(Stream.of(tokenService));

        new AuthenticationTokenServiceRegistrar(provider).afterPropertiesSet();

        assertTrue(AuthenticationTokens.isRegistered());
    }
}
