package io.github.aicyi.midware.web.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PageResponse} 单元测试
 */
class PageResponseTest {

    @Test
    void buildComputesPagesAndNavigation() {
        List<String> list = Arrays.asList("a", "b");

        PageResponse<String> first = PageResponse.build(list, 1, 10, 25);
        assertEquals(Long.valueOf(3), first.getPages());
        assertFalse(first.getHasPrev());
        assertTrue(first.getHasNext());

        PageResponse<String> middle = PageResponse.build(list, 2, 10, 25);
        assertTrue(middle.getHasPrev());
        assertTrue(middle.getHasNext());

        PageResponse<String> last = PageResponse.build(list, 3, 10, 25);
        assertTrue(last.getHasPrev());
        assertFalse(last.getHasNext());
    }

    @Test
    void buildWithEmptyTotal() {
        PageResponse<String> page = PageResponse.build(Collections.emptyList(), 1, 10, 0);

        assertEquals(Long.valueOf(0), page.getPages());
        assertFalse(page.getHasPrev());
        assertFalse(page.getHasNext());
    }

    @Test
    void buildWithZeroSizeDoesNotThrow() {
        PageResponse<String> page = PageResponse.build(Collections.emptyList(), 1, 0, 10);

        assertEquals(Long.valueOf(0), page.getPages());
    }
}
