package io.github.aicyi.commons.core.jwt;

import io.github.aicyi.commons.core.token.TokenManager;
import io.github.aicyi.commons.lang.IJWTInfo;

/**
 * @author Mr.Min
 * @description jwt令牌管理类
 * @date 17:49
 **/
public interface IJwtTokenManager<V extends IJWTInfo> extends TokenManager<String, V> {
}
