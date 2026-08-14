package io.github.aicyi.midware.web.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link CachedBodyRequestWrapper} 单元测试
 */
class CachedBodyRequestWrapperTest {

    @Test
    void bodyRepeatableRead() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent("hello".getBytes(StandardCharsets.UTF_8));

        CachedBodyRequestWrapper wrapper = new CachedBodyRequestWrapper(request);

        assertEquals("hello", StreamUtils.copyToString(wrapper.getInputStream(), StandardCharsets.UTF_8));
        // 第二次读取仍然可用
        assertEquals("hello", StreamUtils.copyToString(wrapper.getInputStream(), StandardCharsets.UTF_8));
    }

    @Test
    void bodyTruncatedWhenExceedLimit() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent("0123456789".getBytes(StandardCharsets.UTF_8));

        CachedBodyRequestWrapper wrapper = new CachedBodyRequestWrapper(request, 4);

        assertEquals(4, wrapper.getContentAsByteArray().length);
        assertEquals("0123", new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    @Test
    void formUrlencodedBodyNotCached() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/x-www-form-urlencoded");
        request.setContent("a=1&b=2".getBytes(StandardCharsets.UTF_8));

        CachedBodyRequestWrapper wrapper = new CachedBodyRequestWrapper(request);

        assertEquals(0, wrapper.getContentAsByteArray().length);
    }

    @Test
    void cacheableJudgement() {
        MockHttpServletRequest multipart = new MockHttpServletRequest();
        multipart.setContentType("multipart/form-data");
        assertFalse(CachedBodyRequestWrapper.cacheable(multipart));

        MockHttpServletRequest form = new MockHttpServletRequest();
        form.setContentType("application/x-www-form-urlencoded");
        assertFalse(CachedBodyRequestWrapper.cacheable(form));

        MockHttpServletRequest json = new MockHttpServletRequest();
        json.setContentType("application/json");
        assertTrue(CachedBodyRequestWrapper.cacheable(json));

        MockHttpServletRequest noContentType = new MockHttpServletRequest();
        assertTrue(CachedBodyRequestWrapper.cacheable(noContentType));
    }
}
