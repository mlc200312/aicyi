package io.github.aicyi.midware.web;

import io.github.aicyi.commons.core.token.TokenService;
import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.lang.exception.UnauthorizedException;
import io.github.aicyi.commons.util.CurrentContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description 身份验证拦截器
 * @date 2021/5/2
 **/
public class AuthInterceptor implements HandlerInterceptor {

    private TokenService<String, IJWTInfo> tokenService;

    public AuthInterceptor(TokenService<String, IJWTInfo> tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
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

        String token = authorization.replace("Bearer ", "");

        if (!tokenService.isValid(token)) {
            throw new UnauthorizedException();
        }

        IJWTInfo jwtInfo = tokenService.parsePrincipal(token);

        CurrentContextHolder.setUserId(jwtInfo.getId());
        CurrentContextHolder.setUsername(jwtInfo.getUniqueName());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
        CurrentContextHolder.remove();
    }
}