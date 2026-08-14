package io.github.aicyi.midware.web.log;

import io.github.aicyi.midware.web.filter.CachedBodyRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link WebRequestLogRecorder} 单元测试
 */
class WebRequestLogRecorderTest {

    @Test
    void getRequestIdConsistentAcrossCalls() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String first = WebRequestLogRecorder.getRequestId(request);
        String second = WebRequestLogRecorder.getRequestId(request);

        assertNotNull(first);
        assertEquals(first, second);
    }

    @Test
    void getRequestIdPrefersExplicitValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        WebRequestLogRecorder.setRequestId(request, "explicit-id");

        assertEquals("explicit-id", WebRequestLogRecorder.getRequestId(request));
        assertEquals("explicit-id", WebRequestLogRecorder.getRequestId(request));
    }

    @Test
    void getRequestIdFromHeaderAndKeepConsistent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(WebRequestLogRecorder.REQUEST_ID_HEADER, "header-id");

        assertEquals("header-id", WebRequestLogRecorder.getRequestId(request));
        // 回填后再次获取保持一致
        assertEquals("header-id", WebRequestLogRecorder.getRequestId(request));
    }

    @Test
    void getRequestIdRejectsMaliciousHeader() {
        // 含控制字符（日志注入）拒绝采纳，自动生成
        MockHttpServletRequest controlCharRequest = new MockHttpServletRequest();
        controlCharRequest.addHeader(WebRequestLogRecorder.REQUEST_ID_HEADER, "id\nfake-log-line");
        String generated = WebRequestLogRecorder.getRequestId(controlCharRequest);
        assertTrue(StringUtils.isNotBlank(generated));
        assertFalse("id\nfake-log-line".equals(generated));

        // 超长拒绝采纳，自动生成
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('a');
        }
        MockHttpServletRequest longRequest = new MockHttpServletRequest();
        longRequest.addHeader(WebRequestLogRecorder.REQUEST_ID_HEADER, sb.toString());
        String longGenerated = WebRequestLogRecorder.getRequestId(longRequest);
        assertTrue(longGenerated.length() <= 64);
    }

    @Test
    void createWithMarkStartResolvesCostTime() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        WebRequestLogRecorder.markStart(request);
        WebRequestLog log = WebRequestLogRecorder.create(request, response);

        assertNotNull(log.getCostTime());
        assertTrue(log.getCostTime() >= 0);
        assertTrue(log.getSuccess());
        assertEquals(Integer.valueOf(200), log.getResponse().getStatusCode());
    }

    @Test
    void createWithoutMarkStartHasNullCostTime() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        WebRequestLog log = WebRequestLogRecorder.create(request, response);

        assertNull(log.getCostTime());
    }

    @Test
    void errorLoggedFlag() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertFalse(WebRequestLogRecorder.isErrorLogged(request));
        WebRequestLogRecorder.markErrorLogged(request);
        assertTrue(WebRequestLogRecorder.isErrorLogged(request));
    }

    @Test
    void sensitiveQueryParamMasked() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("password", "123456");
        request.setParameter("accessToken", "abc");
        request.setParameter("name", "tom");

        WebRequestLog.RequestInfo info = WebRequestLogRecorder.buildRequestInfo(request);

        assertEquals("******", info.getQueryParams().get("password"));
        assertEquals("******", info.getQueryParams().get("accessToken"));
        assertEquals("tom", info.getQueryParams().get("name"));
    }

    @Test
    void bodySensitiveFieldsMasked() throws IOException {
        MockHttpServletRequest raw = new MockHttpServletRequest();
        raw.setContentType("application/json");
        raw.setContent("{\"username\":\"tom\",\"password\":\"secret123\"}".getBytes(StandardCharsets.UTF_8));

        CachedBodyRequestWrapper wrapper = new CachedBodyRequestWrapper(raw);
        WebRequestLog.RequestInfo info = WebRequestLogRecorder.buildRequestInfo(wrapper);

        String body = String.valueOf(info.getBody());
        assertTrue(body.contains("******"));
        assertFalse(body.contains("secret123"));
        assertTrue(body.contains("tom"));
    }

    @Test
    void longBodyTruncated() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3000; i++) {
            sb.append('a');
        }
        MockHttpServletRequest raw = new MockHttpServletRequest();
        raw.setContentType("text/plain");
        raw.setContent(sb.toString().getBytes(StandardCharsets.UTF_8));

        CachedBodyRequestWrapper wrapper = new CachedBodyRequestWrapper(raw);
        WebRequestLog.RequestInfo info = WebRequestLogRecorder.buildRequestInfo(wrapper);

        String body = String.valueOf(info.getBody());
        assertTrue(body.contains("truncated"));
        assertTrue(body.length() < 3000);
    }
}
