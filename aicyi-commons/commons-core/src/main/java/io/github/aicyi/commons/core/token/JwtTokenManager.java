package io.github.aicyi.commons.core.token;

import io.github.aicyi.commons.core.IJWTInfo;

/**
 * @author Mr.Min
 * @description jwt令牌管理类
 * @date 17:49
 **/
public interface JwtTokenManager<V extends IJWTInfo> extends TokenManager<String, V> {
}
