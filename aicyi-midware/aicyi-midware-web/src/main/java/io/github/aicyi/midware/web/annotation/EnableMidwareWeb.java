package io.github.aicyi.midware.web.annotation;

import io.github.aicyi.midware.web.config.MidwareWebConfigurationRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mr.Min
 * @description Web 能力开启注解
 * <p>
 * 标注在 Spring Boot 启动类或任意配置类上，通过 {@link MidwareWebConfigurationRegistrar} 按需装配以下能力：
 * <ul>
 *     <li>鉴权拦截器：身份验证拦截（依赖容器注入的 {@code AuthenticationTokenService}，
 *     接口可通过 {@link IgnoreAuth} 跳过鉴权），可通过 {@link #enableAuth()} 关闭</li>
 *     <li>请求日志拦截器：请求信息日志，请求结束时输出完整请求日志（入参、出参、耗时），
 *     可通过 {@link #enableRequestLog()} 关闭</li>
 *     <li>请求体缓存过滤器：使请求体在任意阶段可重复读取</li>
 *     <li>全局异常处理器：统一异常响应并记录异常请求日志</li>
 * </ul>
 * <p>
 * 注意：同一应用中请仅在一处标注本注解，重复标注时仅首个声明生效并输出告警日志
 * <p>
 * 各能力另支持配置项细粒度关闭（缺省均为开启）：
 * {@code aicyi.web.trace-id.enabled}、{@code aicyi.web.request-log.enabled}、
 * {@code aicyi.web.body-cache.enabled}
 * @date 2020-02-19
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(MidwareWebConfigurationRegistrar.class)
public @interface EnableMidwareWeb {

    /**
     * 是否开启身份验证拦截，默认开启
     * <p>
     * 开启后未标注 {@link IgnoreAuth} 的接口必须携带合法 Bearer Token；
     * 容器中不存在 {@code AuthenticationTokenService} Bean 时启动即失败，防止鉴权配置遗漏流入生产
     */
    boolean enableAuth() default true;

    /**
     * 是否开启请求信息日志，默认开启
     */
    boolean enableRequestLog() default true;

    /**
     * 拦截器排除路径（Ant 风格），匹配的路径同时排除鉴权拦截器与请求日志拦截器（适用于静态资源、接口文档等无需鉴权与日志降噪的路径）
     */
    String[] excludePathPatterns() default {};
}
