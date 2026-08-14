package io.github.aicyi.midware.web;

import io.github.aicyi.commons.core.token.AuthenticationTokens;
import io.github.aicyi.commons.security.token.jwt.IJWTInfo;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.commons.util.CurrentContextHolder;
import org.apache.commons.lang3.StringUtils;
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
 * 校验 Bearer Token 并写入当前用户上下文；请求日志职责由 {@link RequestLogInterceptor} 独立承担
 * @date 2021/5/2
 **/
public class AuthInterceptor implements HandlerInterceptor {

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

        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer")) {
            throw new UnauthorizedException();
        }

        String accessToken = authorization.replace("Bearer ", "");

        if (!AuthenticationTokens.validateAccessToken(accessToken)) {
            throw new UnauthorizedException();
        }

        IJWTInfo jwtInfo = AuthenticationTokens.parsePrincipal(accessToken);

        CurrentContextHolder.setUserId(jwtInfo.getId());
        CurrentContextHolder.setUsername(jwtInfo.getUniqueName());

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception e) {

        CurrentContextHolder.remove();
    }
}
