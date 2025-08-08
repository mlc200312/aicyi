package com.aichuangyi.base.security.jwt;

import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:12
 **/
public interface JwtManager {

    /**
     * 创建令牌
     *
     * @param claimMap
     * @return
     */
    String createToken(Map<String, Object> claimMap);

    /**
     * 验证令牌
     *
     * @param jwt
     * @return
     */
    boolean verifyToken(String jwt);

    /**
     * 刷新令牌
     *
     * @param token
     * @return
     */
    String refreshToken(String token);
}
