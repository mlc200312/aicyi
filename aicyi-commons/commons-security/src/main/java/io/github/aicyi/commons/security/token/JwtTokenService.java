package io.github.aicyi.commons.security.token;

import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.token.TokenService;

/**
 * @author Mr.Min
 * @description jwt令牌管理类
 * @date 17:49
 **/
public interface JwtTokenService<P extends IJWTInfo> extends TokenService<String, P> {
}
