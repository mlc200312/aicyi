package io.github.aicyi.midware.web.filter;

import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Mr.Min
 * @description 链路追踪过滤器：为每个请求建立 traceId 并写入 MDC，供日志 pattern（%X{traceId}）输出。
 * <p>
 * 上游传入 {@value #TRACE_ID_HEADER} 请求头时沿用（网关/跨服务透传），否则本地生成；
 * traceId 同时回写响应头，便于前端/调用方与后端日志对账。
 * <p>
 * finally 中强制 remove：Servlet 容器线程池复用线程，残留 MDC 会污染同一线程的下一个请求
 * @date 2026/8/19
 **/
public class TraceIdFilter implements Filter {

    /**
     * 链路 ID 传输请求头/响应头名称
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * MDC 中的链路 ID 键名，与 logback pattern 的 %X{traceId} 对齐
     */
    public static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String traceId = resolveTraceId(request);

        try {
            MDC.put(MDC_TRACE_ID_KEY, traceId);

            // 回写响应头，非 HTTP 响应（理论不存在）不强加
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse) response).setHeader(TRACE_ID_HEADER, traceId);
            }

            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_TRACE_ID_KEY);
        }
    }

    /**
     * 解析 traceId：上游传入则沿用，否则生成 32 位无横线 UUID
     */
    private String resolveTraceId(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            String upstream = ((HttpServletRequest) request).getHeader(TRACE_ID_HEADER);
            if (upstream != null && !upstream.trim().isEmpty()) {
                return upstream.trim();
            }
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
