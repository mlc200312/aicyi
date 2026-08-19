package io.github.aicyi.midware.web.log;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.logging.LoggerType;
import io.github.aicyi.commons.util.JsonSensitiveMaskUtils;
import io.github.aicyi.commons.util.NumberUtils;
import io.github.aicyi.commons.util.UUIDUtils;
import io.github.aicyi.midware.web.filter.CachedBodyRequestWrapper;
import io.github.aicyi.midware.web.util.CharsetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Web 请求日志记录器
 * <p>
 * 负责将一次 HTTP 请求的入参、出参、耗时与异常信息组装为 {@link WebRequestLog} 并按结果级别输出。
 * <p>
 * 日志安全：敏感参数（如 password、token 等）自动脱敏，请求体超长时自动截断，防止敏感信息泄露与日志膨胀。
 * <p>
 * 典型用法：
 * <pre>
 *     // 请求进入时标记开始时间（如拦截器 preHandle）
 *     WebRequestLogRecorder.markStart(request);
 *     // 请求结束时记录日志（如拦截器 afterCompletion）
 *     WebRequestLogRecorder.record(request, response);
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

    /**
     * 日志输出的请求体最大长度，超出部分截断
     */
    private static final int MAX_LOG_BODY_LENGTH = 2048;

    /**
     * 外部传入请求 ID 的最大长度，超出则拒绝采纳并自动生成，防止恶意超长 Header 污染日志
     */
    private static final int MAX_REQUEST_ID_LENGTH = 64;

    /**
     * 敏感参数名关键字（小写匹配），命中的参数值以 ****** 代替
     */
    private static final List<String> SENSITIVE_KEYWORDS = Arrays.asList(
            "password", "pwd", "passwd", "secret", "token", "credential", "authorization");

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRequestLogRecorder.class);

    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger(LoggerType.ACCESS);

    private static final Logger PERFORMANCE_LOGGER = LoggerFactory.getLogger(LoggerType.PERFORMANCE);

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
     * 判断请求是否慢请求
     *
     * @param request HTTP 请求
     * @return 慢请求时返回 true
     */
    public static boolean isSlowRequest(HttpServletRequest request) {
        Long costTime = resolveCostTime(request);
        return NumberUtils.isPositive(costTime) && costTime > 10000;
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
     * 优先取请求属性，其次取 {@value #REQUEST_ID_HEADER} Header（需通过安全校验），最后自动生成。
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
        if (isAcceptableRequestId(headerRequestId)) {
            request.setAttribute(REQUEST_ID_ATTRIBUTE, headerRequestId);
            return headerRequestId;
        }

        String generatedRequestId = UUIDUtils.generateV7Id();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, generatedRequestId);
        return generatedRequestId;
    }

    /**
     * 校验外部传入的请求 ID 是否可采纳
     * <p>
     * 拒绝空值、超长（防日志膨胀）与含控制字符（防日志注入）的值，不合法时回退自动生成
     *
     * @param requestId 外部请求 ID
     * @return 可采纳时返回 true
     */
    private static boolean isAcceptableRequestId(String requestId) {
        if (StringUtils.isBlank(requestId) || requestId.length() > MAX_REQUEST_ID_LENGTH) {
            return false;
        }

        for (int i = 0; i < requestId.length(); i++) {
            char c = requestId.charAt(i);
            if (c <= 0x1F || c == 0x7F) {
                return false;
            }
        }
        return true;
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

        if (isSlowRequest(request)) {
            PERFORMANCE_LOGGER.info(requestLog);
        }

        if (Boolean.TRUE.equals(requestLog.getSuccess())) {
            ACCESS_LOGGER.info(requestLog);
        } else if (isErrorLogged(request)) {
            // 异常详情已由异常处理器输出，此处仅记录请求生命周期概要，避免重复输出完整异常日志
            LOGGER.warn("error already logged, requestId: {}, costTime: {}ms, statusCode: {}",
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
     * 解析 Query 参数，多值参数保留数组形式，敏感参数值脱敏
     */
    private static Map<String, Object> resolveQueryParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        if (parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> queryParams = new LinkedHashMap<>(parameterMap.size());
        parameterMap.forEach((name, values) -> {
            if (isSensitiveName(name)) {
                queryParams.put(name, "******");
            } else {
                queryParams.put(name, values != null && values.length == 1 ? values[0] : values);
            }
        });

        return queryParams;
    }

    /**
     * 解析请求体并做脱敏与截断
     * <p>
     * 优先从 {@link CachedBodyRequestWrapper} 预缓存读取（任意阶段可用），
     * 兼容 {@link ContentCachingRequestWrapper} 懒缓存（仅在请求体已被消费后有效）。
     */
    private static String resolveBody(HttpServletRequest request) {
        String body = readBody(request);
        return truncateBody(maskSensitiveBody(body));
    }

    /**
     * 读取原始请求体文本
     */
    private static String readBody(HttpServletRequest request) {
        if (request instanceof CachedBodyRequestWrapper) {
            byte[] bodyBytes = ((CachedBodyRequestWrapper) request).getContentAsByteArray();
            return bodyBytes.length == 0 ? "" : new String(bodyBytes, CharsetUtils.resolveCharset(request.getCharacterEncoding()));
        }

        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] bodyBytes = wrapper.getContentAsByteArray();
            return bodyBytes.length == 0 ? "" : new String(bodyBytes, CharsetUtils.resolveCharset(wrapper.getCharacterEncoding()));
        }

        return "";
    }

    /**
     * 判断参数名是否命中敏感关键字
     */
    private static boolean isSensitiveName(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        String lowerCaseName = name.toLowerCase();
        return SENSITIVE_KEYWORDS.stream().anyMatch(lowerCaseName::contains);
    }

    /**
     * 对请求体中 JSON 风格的敏感字段值脱敏
     */
    private static String maskSensitiveBody(String body) {
        if (StringUtils.isBlank(body)) {
            return body;
        }

        return JsonSensitiveMaskUtils.maskJsonBody(body);
    }

    /**
     * 请求体超长时截断，防止日志膨胀
     */
    private static String truncateBody(String body) {
        if (StringUtils.isEmpty(body) || body.length() <= MAX_LOG_BODY_LENGTH) {
            return body;
        }

        return body.substring(0, MAX_LOG_BODY_LENGTH) + "...(truncated, totalLength=" + body.length() + ")";
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
