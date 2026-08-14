package io.github.aicyi.midware.web.auth;

import io.github.aicyi.commons.core.token.AuthenticationTokens;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.commons.util.CurrentContextHolder;
import io.github.aicyi.midware.web.annotation.IgnoreAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 身份验证拦截器
 * <p>
 * 校验 Bearer Token 并通过 {@link AuthenticatedPrincipalHandler} 写入当前用户上下文；
 * 请求日志职责由请求日志拦截器独立承担
 * @date 2021/5/2
 **/
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Bearer Token 标准前缀（含分隔空格）
     */
    private static final String BEARER_PREFIX = "Bearer ";

    private final ObjectProvider<AuthenticatedPrincipalHandler> principalHandlerProvider;

    private final AuthenticatedPrincipalHandler defaultPrincipalHandler = new JwtPrincipalHandler();

    public AuthInterceptor(ObjectProvider<AuthenticatedPrincipalHandler> principalHandlerProvider) {
        this.principalHandlerProvider = principalHandlerProvider;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        // Token 服务未注册时不进行身份验证，直接放行
        if (!AuthenticationTokens.isRegistered() || !(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 配置该注解，说明不进行用户拦截
        IgnoreAuth annotation = handlerMethod.getBeanType().getAnnotation(IgnoreAuth.class);
        // 有 IgnoreAuth 就放行
        if (Objects.nonNull(annotation) || handlerMethod.hasMethodAnnotation(IgnoreAuth.class)) {
            return true;
        }

        // 否则必须带 token
        String authorization = request.getHeader("Authorization");

        // 严格校验 Bearer 前缀（含空格），避免 Bearerabc 等畸形头进入解析环节
        if (StringUtils.isBlank(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException();
        }

        String accessToken = authorization.substring(BEARER_PREFIX.length()).trim();

        if (StringUtils.isBlank(accessToken)) {
            throw new UnauthorizedException();
        }

        if (!AuthenticationTokens.validateAccessToken(accessToken)) {
            throw new UnauthorizedException();
        }

        Object principal = AuthenticationTokens.parsePrincipal(accessToken);

        // 通过 SPI 写入用户上下文，默认处理 IJWTInfo 主体，业务可替换
        principalHandlerProvider.getIfAvailable(() -> defaultPrincipalHandler).handle(principal);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception e) {

        CurrentContextHolder.remove();
    }
}
