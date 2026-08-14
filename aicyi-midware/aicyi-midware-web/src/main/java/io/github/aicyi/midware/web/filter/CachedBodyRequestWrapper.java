package io.github.aicyi.midware.web.filter;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 请求体预缓存包装器
 * <p>
 * 与 Spring 的 {@link org.springframework.web.util.ContentCachingRequestWrapper} 懒缓存不同，
 * 本包装器在构造时立即将请求体读入内存缓存，使请求体可在任意阶段（拦截器、异常处理器等）被重复读取。
 * <p>
 * 缓存大小受 {@link #DEFAULT_MAX_CACHED_BODY_SIZE} 限制，超限部分不再读入内存，防止超大请求体造成内存压力。
 * <p>
 * 以下类型请求不做缓存（由 {@link CachingRequestBodyFilter} 判断后直接放行原始请求，避免其输入流被包装后吞掉）：
 * <ul>
 *     <li>multipart/*：通常为文件上传，体积大且不适合文本化记录</li>
 *     <li>application/x-www-form-urlencoded：需保留容器对表单参数的解析能力</li>
 * </ul>
 *
 * @author Mr.Min
 * @date 2026/8/13
 */
public class CachedBodyRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 默认请求体缓存大小上限（字节），超出部分将被截断，不再读入内存
     */
    public static final int DEFAULT_MAX_CACHED_BODY_SIZE = 256 * 1024;

    private final byte[] cachedBody;

    public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
        this(request, DEFAULT_MAX_CACHED_BODY_SIZE);
    }

    /**
     * @param request           HTTP 请求
     * @param maxCachedBodySize 请求体缓存大小上限（字节），超出部分将被截断；非法值回退 {@link #DEFAULT_MAX_CACHED_BODY_SIZE}
     */
    public CachedBodyRequestWrapper(HttpServletRequest request, int maxCachedBodySize) throws IOException {
        super(request);
        int limit = maxCachedBodySize > 0 ? maxCachedBodySize : DEFAULT_MAX_CACHED_BODY_SIZE;
        this.cachedBody = cacheable(request) ? cacheBody(request, limit) : new byte[0];
    }

    /**
     * 读取请求体至缓存，最多读取 {@code maxCachedBodySize} 字节
     */
    private static byte[] cacheBody(HttpServletRequest request, int maxCachedBodySize) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.min(maxCachedBodySize, 8192));
        StreamUtils.copyRange(request.getInputStream(), out, 0, maxCachedBodySize - 1);
        return out.toByteArray();
    }

    /**
     * 获取缓存的请求体字节数据
     *
     * @return 请求体字节数据，无缓存时为空数组
     */
    public byte[] getContentAsByteArray() {
        return cachedBody;
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), resolveCharset(getCharacterEncoding())));
    }

    /**
     * 判断请求体是否需要缓存
     *
     * @param request HTTP 请求
     * @return 需要缓存时返回 true
     */
    public static boolean cacheable(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return true;
        }

        String lowerCaseContentType = contentType.toLowerCase();
        return !lowerCaseContentType.startsWith("multipart/")
                && !lowerCaseContentType.startsWith("application/x-www-form-urlencoded");
    }

    /**
     * 解析字符集，非法或未指定时回退 UTF-8
     */
    private static Charset resolveCharset(String encoding) {
        if (encoding == null || encoding.isEmpty()) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }

    /**
     * 基于缓存字节数组的输入流，每次 {@link #getInputStream()} 调用都返回新的流，支持重复读取
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream delegate;

        CachedBodyServletInputStream(byte[] cachedBody) {
            this.delegate = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return delegate.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("ReadListener is not supported");
        }

        @Override
        public int read() {
            return delegate.read();
        }
    }
}
