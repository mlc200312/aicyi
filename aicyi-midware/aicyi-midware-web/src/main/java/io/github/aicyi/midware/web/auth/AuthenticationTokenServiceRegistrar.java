package io.github.aicyi.midware.web.auth;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import io.github.aicyi.commons.core.token.AuthenticationTokens;
import io.github.aicyi.commons.logging.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mr.Min
 * @description {@link AuthenticationTokenService} 注册器
 * <p>
 * 应用启动时将容器中的 Token 服务实例注册到 {@link AuthenticationTokens} 工具，
 * 使业务代码无需注入即可通过工具静态调用。
 * <p>
 * 容器中不存在该 Bean 时静默跳过；存在多个时仅注册第一个并输出告警日志
 * @date 2026/8/13
 **/
public class AuthenticationTokenServiceRegistrar implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationTokenServiceRegistrar.class);

    private final ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider;

    public AuthenticationTokenServiceRegistrar(ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider) {
        this.tokenServiceProvider = tokenServiceProvider;
    }

    @Override
    public void afterPropertiesSet() {
        List<AuthenticationTokenService<?>> tokenServices = tokenServiceProvider.stream().collect(Collectors.toList());
        if (tokenServices.isEmpty()) {
            return;
        }

        if (tokenServices.size() > 1) {
            LOGGER.warn("Found {} AuthenticationTokenService beans, only the first one will be registered: {}",
                    tokenServices.size(), tokenServices);
        }

        AuthenticationTokens.register(tokenServices.get(0));
    }
}
