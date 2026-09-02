package io.github.aicyi.midware.web.log;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Mr.Min
 * @description 请求信息日志拦截器
 * <p>
 * 请求开始时标记开始时间（用于计算耗时），请求结束时输出完整 {@link WebRequestLog}（入参、出参、耗时）。
 * 应先于鉴权拦截器注册，保证鉴权失败时也能记录请求日志
 * @date 2026/8/13
 **/
public class RequestLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {

        // 标记请求开始时间，用于计算响应耗时
        WebRequestLogRecorder.markStart(request);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception e) {

        WebRequestLogRecorder.record(request, response);
    }
}
