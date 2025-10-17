package io.github.aicyi.midware.mybatisplus;

import io.github.aicyi.commons.lang.BaseBean;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 16:56
 **/
public class PageResult<T> extends BaseBean {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setContent(page.getContent());
        result.setTotalPages(page.getTotalPages());
        result.setTotalElements(page.getTotalElements());
        result.setPageNumber(page.getNumber());
        result.setPageSize(page.getSize());
        return result;
    }
}
