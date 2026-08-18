package io.github.aicyi.commons.core.token;


import java.util.Map;
import java.util.Set;

/**
 * 认证 Token 工具
 * <p>
 * 对 {@link AuthenticationTokenService} 的静态封装，使业务代码无需注入服务实例即可调用 Token 能力。
 * <p>
 * 服务实例需在应用启动时通过 {@link #register(AuthenticationTokenService)} 注册
 * （框架已提供自动注册组件），未注册时调用任意方法将抛出 {@link IllegalStateException}。
 *
 * @author Mr.Min
 * @date 2026/8/13
 */
public final class AuthenticationTokens {

    private static volatile AuthenticationTokenService<?> tokenService;

    private AuthenticationTokens() {
    }

    /**
     * 注册 Token 服务实例（应用启动阶段调用一次）
     *
     * @param service Token 服务实例
     */
    public static void register(AuthenticationTokenService<?> service) {
        tokenService = service;
    }

    /**
     * 判断 Token 服务是否已注册
     *
     * @return 已注册返回 true
     */
    public static boolean isRegistered() {
        return tokenService != null;
    }

    /**
     * 获取已注册的 Token 服务实例，适用于需要直接调用扩展能力的场景
     *
     * @return Token 服务实例
     * @throws IllegalStateException 未注册时抛出
     */
    @SuppressWarnings("unchecked")
    public static <P> AuthenticationTokenService<P> getService() {
        AuthenticationTokenService<?> service = tokenService;
        if (service == null) {
            throw new IllegalStateException("AuthenticationTokenService not registered");
        }
        return (AuthenticationTokenService<P>) service;
    }

    /**
     * 创建 Token
     *
     * @param principal  Principal
     * @param attributes 自定义属性
     * @return TokenPair
     */
    public static <P> TokenPair createToken(P principal, Map<String, Object> attributes) {
        return AuthenticationTokens.<P>getService().createToken(principal, attributes);
    }

    /**
     * 刷新 AccessToken
     *
     * @param refreshToken RefreshToken
     * @return TokenPair
     */
    public static TokenPair refreshToken(String refreshToken) {
        return getService().refreshToken(refreshToken);
    }

    /**
     * 获取在线 RefreshToken
     *
     * @param principal Principal
     * @return RefreshToken 集合
     */
    public static <P> Set<String> getRefreshTokens(P principal) {
        return AuthenticationTokens.<P>getService().getRefreshTokens(principal);
    }

    /**
     * 退出登录
     *
     * @param refreshToken RefreshToken
     */
    public static void revokeToken(String refreshToken) {
        getService().revokeToken(refreshToken);
    }

    /**
     * 校验 AccessToken
     *
     * @param accessToken AccessToken
     * @return 是否有效
     */
    public static boolean validateAccessToken(String accessToken) {
        return getService().validateAccessToken(accessToken);
    }

    /**
     * 解析 Principal
     *
     * @param accessToken AccessToken
     * @return Principal
     */
    public static <P> P parsePrincipal(String accessToken) {
        return AuthenticationTokens.<P>getService().parsePrincipal(accessToken);
    }

    /**
     * 获取自定义属性
     *
     * @param accessToken AccessToken
     * @return 自定义属性
     */
    public static Map<String, Object> getAttributes(String accessToken) {
        return getService().getAttributes(accessToken);
    }
}
