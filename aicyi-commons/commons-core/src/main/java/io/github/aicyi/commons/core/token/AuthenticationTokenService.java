package io.github.aicyi.commons.core.token;

import java.util.Map;

/**
 * 认证Token服务
 *
 * @param <P> Principal类型
 * @author Mr.Min
 */
public interface AuthenticationTokenService<P> {

    /**
     * 创建Token
     *
     * @param principal  Principal
     * @param attributes 自定义属性
     * @return TokenPair
     */
    TokenPair createToken(P principal, Map<String, Object> attributes);

    /**
     * 刷新AccessToken
     *
     * @param refreshToken RefreshToken
     * @return TokenPair
     */
    TokenPair refreshToken(String refreshToken);

    /**
     * 校验AccessToken
     *
     * @param accessToken AccessToken
     * @return 是否有效
     */
    boolean validateAccessToken(String accessToken);

    /**
     * 解析Principal
     *
     * @param accessToken AccessToken
     * @return Principal
     */
    P parsePrincipal(String accessToken);

    /**
     * 获取自定义属性
     *
     * @param accessToken
     * @return
     */
    Map<String, Object> getAttributes(String accessToken);

    /**
     * 退出登录
     *
     * @param refreshToken RefreshToken
     */
    void revokeToken(String refreshToken);
}