package io.github.aicyi.midware.web.auth;

import io.github.aicyi.commons.security.token.jwt.IJWTInfo;
import io.github.aicyi.commons.util.CurrentContextHolder;

/**
 * @author Mr.Min
 * @description JWT 主体处理器（默认实现）
 * <p>
 * 将 IJWTInfo 主体中的用户 ID 与用户名写入 {@link CurrentContextHolder}；
 * 主体类型不匹配时跳过（适用于业务自定义 Token 实现但未提供自定义处理器的降级场景）
 * @date 2026/8/14
 **/
public class JwtPrincipalHandler implements AuthenticatedPrincipalHandler {

    @Override
    public void handle(Object principal) {
        if (!(principal instanceof IJWTInfo)) {
            return;
        }

        IJWTInfo jwtInfo = (IJWTInfo) principal;
        CurrentContextHolder.setUserId(jwtInfo.getId());
        CurrentContextHolder.setUsername(jwtInfo.getUniqueName());
    }
}
