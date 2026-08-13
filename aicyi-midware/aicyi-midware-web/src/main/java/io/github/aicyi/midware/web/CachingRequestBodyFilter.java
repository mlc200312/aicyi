package io.github.aicyi.midware.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Mr.Min
 * @description 请求体缓存过滤器，使请求体在任意阶段可重复读取
 * @date 2026/8/12
 **/
public class CachingRequestBodyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 已包装则不重复缓存
        if (httpRequest instanceof CachedBodyRequestWrapper) {
            chain.doFilter(request, response);
            return;
        }

        // 包装原始请求，立即缓存请求体
        chain.doFilter(new CachedBodyRequestWrapper(httpRequest), response);
    }
}