package io.github.aicyi.midware.web;

import io.github.aicyi.commons.lang.BaseBean;
import io.swagger.annotations.ApiModel;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Mr.Min
 * @description 分页响应对象
 * @date 14:24
 **/
@ApiModel("分页响应对象")
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
     * @param list
     * @param page
     * @param size
     * @param total
     * @param <E>
     * @return
     */
    public static <E> PageResponse<E> build(List<E> list, int page, int size, long total) {
        PageResponse<E> pageResponse = new PageResponse<>();
        pageResponse.setList(list);
        pageResponse.setPage(page);
        pageResponse.setSize(size);
        pageResponse.setTotal(total);

        long pages = (total + size - 1) / size;
        pageResponse.setPages(pages);
        pageResponse.setHasPrev(page > 1);
        pageResponse.setHasNext(page < pages);
        return pageResponse;
    }

    /**
     * 构建分页结果
     *
     * @param <E>
     * @param page
     * @return
     */
    public static <E> PageResponse<E> build(List<E> list, Page page) {
        return build(list, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }
}
