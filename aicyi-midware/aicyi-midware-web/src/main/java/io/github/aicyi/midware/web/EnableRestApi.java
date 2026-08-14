package io.github.aicyi.midware.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Mr.Min
 * @description Web 能力开启注解
 * <p>
 * 标注在 Spring Boot 启动类或任意配置类上，自动装配以下能力：
 * <ul>
 *     <li>{@link AuthInterceptor}：身份验证拦截（依赖 AuthenticationTokens 工具，
 *     接口可通过 {@link IgnoreAuth} 跳过鉴权），可通过 {@link #enableAuth()} 关闭</li>
 *     <li>{@link RequestLogInterceptor}：请求信息日志，请求结束时输出完整 {@link WebRequestLog}（入参、出参、耗时），
 *     可通过 {@link #enableRequestLog()} 关闭</li>
 *     <li>{@link CachingRequestBodyFilter}：请求体缓存过滤器，使请求体在任意阶段可重复读取</li>
 *     <li>{@link GlobalExceptionHandler}：全局异常处理器，统一异常响应并记录异常请求日志，
 *     可通过 {@link #enableGlobalExceptionHandler()} 关闭</li>
 * </ul>
 * @date 2020-02-19
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RestApiWebMvcConfiguration.class, RestApiExceptionConfiguration.class, RestApiAuthenticationConfiguration.class})
public @interface EnableRestApi {

    /**
     * 是否注入 {@link GlobalExceptionHandler} 全局异常处理器，默认注入
     * <p>
     * 设为 false 时不注入，适用于业务方自定义统一异常处理（如已有其他 {@code @RestControllerAdvice}）的场景
     */
    boolean enableGlobalExceptionHandler() default true;

    /**
     * 是否开启身份验证拦截，默认开启
     * <p>
     * 开启后未标注 {@link IgnoreAuth} 的接口必须携带合法 Bearer Token；
     * 容器中不存在 {@code AuthenticationTokenService<IJWTInfo>} Bean 时自动降级为仅记录请求日志
     */
    boolean enableAuth() default true;

    /**
     * 是否开启请求信息日志（{@link RequestLogInterceptor}），默认开启
     */
    boolean enableRequestLog() default true;

    /**
     * 拦截器排除路径（Ant 风格），匹配的路径不进行身份验证，如静态资源、接口文档
     */
    String[] authExcludePathPatterns() default {};
}
