package io.github.aicyi.midware.web.auth;

/**
 * @author Mr.Min
 * @description 鉴权通过后主体处理器 SPI
 * <p>
 * {@link AuthInterceptor} 校验 Token 通过后回调本接口，负责将主体信息（如 userId、username）写入当前上下文。
 * 默认实现 {@link JwtPrincipalHandler} 处理 IJWTInfo 主体；业务方可注册自定义 Bean 替换默认实现，
 * 以支持非 JWT 的主体类型，避免 Web 层与具体 Token 实现强耦合
 * @date 2026/8/14
 **/
public interface AuthenticatedPrincipalHandler {

    /**
     * 处理鉴权通过后解析出的主体
     *
     * @param principal Token 中解析出的主体对象
     */
    void handle(Object principal);
}
