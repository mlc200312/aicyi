package io.github.aicyi.midware.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Mr.Min
 * @description 请求体缓存过滤器，使请求体在任意阶段可重复读取
 * <p>
 * 不可缓存的请求（multipart / form 表单等）不包装，直接放行原始请求，
 * 避免包装后吞掉请求体输入流
 * @date 2026/8/12
 **/
public class CachingRequestBodyFilter implements Filter {

    /**
     * 请求体缓存大小上限（字节），超出部分将被截断
     */
    private int maxCachedBodySize = CachedBodyRequestWrapper.DEFAULT_MAX_CACHED_BODY_SIZE;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 非 HTTP 请求直接放行原始请求，避免强转抛 ClassCastException
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 已包装则不重复缓存；不可缓存的请求直接放行原始请求
        if (httpRequest instanceof CachedBodyRequestWrapper || !CachedBodyRequestWrapper.cacheable(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 包装原始请求，立即缓存请求体（受大小上限截断）
        chain.doFilter(new CachedBodyRequestWrapper(httpRequest, maxCachedBodySize), response);
    }

    /**
     * 设置请求体缓存大小上限（字节），超出部分将被截断；非法值回退默认上限
     *
     * @param maxCachedBodySize 缓存大小上限
     */
    public void setMaxCachedBodySize(int maxCachedBodySize) {
        if (maxCachedBodySize > 0) {
            this.maxCachedBodySize = maxCachedBodySize;
        }
    }
}
