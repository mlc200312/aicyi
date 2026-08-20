package io.github.aicyi.midware.web.auth;

import io.github.aicyi.commons.core.token.AuthenticationTokenService;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.commons.util.context.CurrentContextHolder;
import io.github.aicyi.midware.web.annotation.IgnoreAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
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
 * Token 服务通过容器注入（不依赖静态注册状态），请求日志职责由请求日志拦截器独立承担
 * @date 2021/5/2
 **/
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Bearer Token 标准前缀（含分隔空格）
     */
    private static final String BEARER_PREFIX = "Bearer ";

    private final ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider;

    private final ObjectProvider<AuthenticatedPrincipalHandler> principalHandlerProvider;

    private final AuthenticatedPrincipalHandler defaultPrincipalHandler = new JwtPrincipalHandler();

    public AuthInterceptor(ObjectProvider<AuthenticationTokenService<?>> tokenServiceProvider,
                           ObjectProvider<AuthenticatedPrincipalHandler> principalHandlerProvider) {
        this.tokenServiceProvider = tokenServiceProvider;
        this.principalHandlerProvider = principalHandlerProvider;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        // 非控制器处理器（静态资源等）不做鉴权，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // Token 服务缺失时快速失败而非放行：enableAuth=true 时启动期已拦截该场景，
        // 运行期出现说明装配错误，静默放行会导致鉴权整体失效（fail-closed）
        AuthenticationTokenService<?> tokenService = tokenServiceProvider.getIfAvailable();
        if (tokenService == null) {
            throw new IllegalStateException("AuthInterceptor requires an AuthenticationTokenService bean, but none is available. "
                    + "Please provide one or disable auth via @EnableMidwareWeb(enableAuth = false)");
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 配置该注解，说明不进行用户拦截
        IgnoreAuth annotation = handlerMethod.getBeanType().getAnnotation(IgnoreAuth.class);
        // 有 IgnoreAuth 就放行
        if (Objects.nonNull(annotation) || handlerMethod.hasMethodAnnotation(IgnoreAuth.class)) {
            return true;
        }

        // 否则必须带 token
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 严格校验 Bearer 前缀（含空格），避免 Bearerabc 等畸形头进入解析环节
        if (StringUtils.isBlank(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException();
        }

        String accessToken = authorization.substring(BEARER_PREFIX.length()).trim();

        if (StringUtils.isBlank(accessToken)) {
            throw new UnauthorizedException();
        }

        if (!tokenService.validateAccessToken(accessToken)) {
            throw new UnauthorizedException();
        }

        Object principal = tokenService.parsePrincipal(accessToken);

        // 通过 SPI 写入用户上下文，默认处理 IJWTInfo 主体，业务可替换
        principalHandlerProvider.getIfAvailable(() -> defaultPrincipalHandler).handle(principal);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception e) {

        CurrentContextHolder.remove();
    }
}
