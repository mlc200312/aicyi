package io.github.aicyi.midware.web.util;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.UUIDUtils;
import io.github.aicyi.midware.web.CachedBodyRequestWrapper;
import io.github.aicyi.midware.web.WebRequestLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web 请求日志记录器
 * <p>
 * 负责将一次 HTTP 请求的入参、出参、耗时与异常信息组装为 {@link WebRequestLog} 并按结果级别输出。
 * <p>
 * 典型用法：
 * <pre>
 *     // 请求进入时标记开始时间（如拦截器 preHandle）
 *     WebRequestLogRecorder.markStart(request);
 *     // 请求结束时记录日志（如拦截器 afterCompletion）
 *     WebRequestLogRecorder.record(request, response, error);
 * </pre>
 *
 * @author Mr.Min
 * @date 2026/8/12
 */
public final class WebRequestLogRecorder {

    /**
     * 请求 ID Header 名称
     */
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    /**
     * 请求开始时间属性名（仅模块内部约定，外部请通过 {@link #markStart(HttpServletRequest)} 标记）
     */
    private static final String START_TIME_ATTRIBUTE = WebRequestLogRecorder.class.getName() + ".startTime";

    /**
     * 请求 ID 属性名（仅模块内部约定，外部可通过 {@link #setRequestId(HttpServletRequest, String)} 覆盖）
     */
    private static final String REQUEST_ID_ATTRIBUTE = WebRequestLogRecorder.class.getName() + ".requestId";

    /**
     * 异常已记录日志标记属性名（仅模块内部约定，外部请通过 {@link #markErrorLogged(HttpServletRequest)} 标记）
     */
    private static final String ERROR_LOGGED_ATTRIBUTE = WebRequestLogRecorder.class.getName() + ".errorLogged";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRequestLogRecorder.class);

    private WebRequestLogRecorder() {
    }

    /**
     * 标记请求开始时间，用于在记录日志时计算耗时
     *
     * @param request HTTP 请求
     */
    public static void markStart(HttpServletRequest request) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
    }

    /**
     * 显式指定请求 ID（如网关透传场景），未指定时按 Header、自动生成的顺序解析
     *
     * @param request   HTTP 请求
     * @param requestId 请求 ID
     */
    public static void setRequestId(HttpServletRequest request, String requestId) {
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
    }

    /**
     * 标记请求异常日志已输出（如全局异常处理器已记录），供 {@link #record} 判断以避免重复输出完整异常日志
     *
     * @param request HTTP 请求
     */
    public static void markErrorLogged(HttpServletRequest request) {
        request.setAttribute(ERROR_LOGGED_ATTRIBUTE, Boolean.TRUE);
    }

    /**
     * 判断请求异常日志是否已输出
     *
     * @param request HTTP 请求
     * @return 已通过 {@link #logError(HttpServletRequest, Exception)} 或 {@link #markErrorLogged(HttpServletRequest)} 标记时返回 true
     */
    public static boolean isErrorLogged(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute(ERROR_LOGGED_ATTRIBUTE));
    }

    /**
     * 解析请求 ID
     * <p>
     * 优先取请求属性，其次取 {@value #REQUEST_ID_HEADER} Header，最后自动生成。
     * 首次解析结果会回填请求属性，保证同一请求在任意阶段（如异常处理器与拦截器）获取的 ID 一致。
     *
     * @param request HTTP 请求
     * @return 请求 ID
     */
    public static String getRequestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(REQUEST_ID_ATTRIBUTE);
        if (requestId instanceof String && StringUtils.isNotBlank((String) requestId)) {
            return (String) requestId;
        }

        String headerRequestId = request.getHeader(REQUEST_ID_HEADER);
        if (StringUtils.isNotBlank(headerRequestId)) {
            request.setAttribute(REQUEST_ID_ATTRIBUTE, headerRequestId);
            return headerRequestId;
        }

        String generatedRequestId = UUIDUtils.generateV7Id();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, generatedRequestId);
        return generatedRequestId;
    }

    /**
     * 构建请求信息
     *
     * @param request HTTP 请求
     * @return 请求信息
     */
    public static WebRequestLog.RequestInfo buildRequestInfo(HttpServletRequest request) {
        return WebRequestLog.RequestInfo.builder()
                .url(request.getRequestURL().toString())
                .method(request.getMethod())
                .queryParams(resolveQueryParams(request))
                .body(resolveBody(request))
                .build();
    }

    /**
     * 组装请求日志
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @return 请求日志
     */
    public static WebRequestLog create(HttpServletRequest request, HttpServletResponse response) {

        return WebRequestLog.builder()
                .requestId(getRequestId(request))
                .request(buildRequestInfo(request))
                .response(buildResponseInfo(response))
                .costTime(resolveCostTime(request))
                .success(isSuccessful(response))
                .errorMessage(null)
                .build();
    }


    /**
     * 记录请求日志：成功输出 INFO，失败输出 ERROR 并附带异常堆栈
     * <p>
     * 若异常已被异常处理器记录（见 {@link #logError(HttpServletRequest, Exception)}），
     * 则不再重复输出完整异常日志，仅以 INFO 输出一条简要生命周期日志（含耗时与状态码）。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     */
    public static void record(HttpServletRequest request, HttpServletResponse response) {

        WebRequestLog requestLog = create(request, response);

        if (Boolean.TRUE.equals(requestLog.getSuccess())) {
            LOGGER.info(requestLog);
        } else if (isErrorLogged(request)) {
            // 异常详情已由异常处理器输出，此处仅记录请求生命周期概要，避免重复输出完整异常日志
            LOGGER.info("error already logged, requestId: {}, costTime: {}ms, statusCode: {}",
                    requestLog.getRequestId(), requestLog.getCostTime(),
                    requestLog.getResponse() != null ? requestLog.getResponse().getStatusCode() : null);
        } else {
            LOGGER.error(requestLog);
        }
    }

    /**
     * 记录异常请求日志：输出请求 ID、请求信息与异常堆栈，适用于异常处理器等无法获取响应的场景
     *
     * @param request HTTP 请求
     * @param error   异常
     */
    public static void logError(HttpServletRequest request, Exception error) {
        LOGGER.error(error, "requestId: {}, request: {}", getRequestId(request), buildRequestInfo(request));
        markErrorLogged(request);
    }

    /**
     * 构建响应信息
     */
    private static WebRequestLog.ResponseInfo buildResponseInfo(HttpServletResponse response) {
        return WebRequestLog.ResponseInfo.builder()
                .statusCode(response.getStatus())
                .build();
    }

    /**
     * 解析 Query 参数，多值参数保留数组形式
     */
    private static Map<String, Object> resolveQueryParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        if (parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> queryParams = new LinkedHashMap<>(parameterMap.size());
        parameterMap.forEach((name, values) ->
                queryParams.put(name, values != null && values.length == 1 ? values[0] : values));

        return queryParams;
    }

    /**
     * 解析请求体
     * <p>
     * 优先从 {@link CachedBodyRequestWrapper} 预缓存读取（任意阶段可用），
     * 兼容 {@link ContentCachingRequestWrapper} 懒缓存（仅在请求体已被消费后有效）。
     */
    private static String resolveBody(HttpServletRequest request) {
        if (request instanceof CachedBodyRequestWrapper) {
            byte[] bodyBytes = ((CachedBodyRequestWrapper) request).getContentAsByteArray();
            return bodyBytes.length == 0 ? "" : new String(bodyBytes, resolveCharset(request.getCharacterEncoding()));
        }

        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] bodyBytes = wrapper.getContentAsByteArray();
            return bodyBytes.length == 0 ? "" : new String(bodyBytes, resolveCharset(wrapper.getCharacterEncoding()));
        }

        return "";
    }

    /**
     * 解析字符集，非法或未指定时回退 UTF-8
     */
    private static Charset resolveCharset(String encoding) {
        if (StringUtils.isBlank(encoding)) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }

    /**
     * 解析请求耗时
     *
     * @param request HTTP 请求
     * @return 请求耗时，单位：ms；未通过 {@link #markStart(HttpServletRequest)} 标记时返回 null
     */
    private static Long resolveCostTime(HttpServletRequest request) {
        Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime instanceof Long) {
            return System.currentTimeMillis() - (Long) startTime;
        }
        return null;
    }

    /**
     * 判断请求是否成功：无异常且响应状态码小于 400
     */
    private static boolean isSuccessful(HttpServletResponse response) {
        return response.getStatus() < 400;
    }
}
