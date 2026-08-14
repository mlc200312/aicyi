package io.github.aicyi.midware.web.model;

import io.github.aicyi.commons.lang.model.BaseBean;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Mr.Min
 * @description 分页响应对象
 * @date 14:24
 **/
public class PageResponse<E> extends BaseBean {
    /**
     * 当前页数据列表
     */
    private List<E> list;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    private Boolean hasPrev;

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(Boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    /**
     * 构建分页结果
     *
     * @param list  当前页数据列表
     * @param page  当前页码（从 1 开始）
     * @param size  每页条数
     * @param total 总记录数
     * @return 分页结果
     */
    public static <E> PageResponse<E> build(List<E> list, int page, int size, long total) {
        PageResponse<E> pageResponse = new PageResponse<>();
        pageResponse.setList(list);
        pageResponse.setPage(page);
        pageResponse.setSize(size);
        pageResponse.setTotal(total);

        long pages = size > 0 ? (total + size - 1) / size : 0;
        pageResponse.setPages(pages);
        pageResponse.setHasPrev(page > 1);
        pageResponse.setHasNext(page < pages);
        return pageResponse;
    }

    /**
     * 构建分页结果
     *
     * @param list 当前页数据列表
     * @param page Spring Data 分页对象
     * @return 分页结果
     */
    public static <E> PageResponse<E> build(List<E> list, Page page) {
        return build(list, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }
}
