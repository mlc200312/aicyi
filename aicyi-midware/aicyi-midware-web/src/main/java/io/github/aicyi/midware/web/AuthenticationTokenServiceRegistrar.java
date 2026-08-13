package io.github.aicyi.midware.web;

import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import io.github.aicyi.commons.core.token.AuthenticationTokens;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;

/**
 * @author Mr.Min
 * @description {@link AuthenticationTokenService} 注册器
 * <p>
 * 应用启动时将容器中的 Token 服务实例注册到 {@link AuthenticationTokens} 工具，
 * 使业务代码无需注入即可通过工具静态调用；容器中不存在该 Bean 时静默跳过
 * @date 2026/8/13
 **/
public class AuthenticationTokenServiceRegistrar implements InitializingBean {

    private final ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider;

    public AuthenticationTokenServiceRegistrar(ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider) {
        this.tokenServiceProvider = tokenServiceProvider;
    }

    @Override
    public void afterPropertiesSet() {
        AuthenticationTokenService<?> tokenService = tokenServiceProvider.getIfAvailable();
        if (tokenService != null) {
            AuthenticationTokens.register(tokenService);
        }
    }
}
