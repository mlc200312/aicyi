package io.github.aicyi.commons.lang.model;

import java.util.List;

/**
 * @author Mr.Min
 * @description 通用分页结果对象，与 {@link PageParam} 分页参数配套；
 * <p>
 * 分工说明：通用服务层/接口层分页响应；Web 层如需附加 hasNext/hasPrev 翻页标记，
 * 可使用 aicyi-midware-web 的 {@code PageResponse} 包装转换
 * @date 10:48
 **/
public class PageResult<E> extends BaseBean {

    /**
     * 当前页数据列表
     */
    private List<E> list;

    /**
     * 当前页码（从 1 开始）
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
     * 构建分页结果
     *
     * @param list  当前页数据列表
     * @param page  当前页码（从 1 开始）
     * @param size  每页条数
     * @param total 总记录数
     */
    public static <E> PageResult<E> build(List<E> list, int page, int size, long total) {
        PageResult<E> pageResult = new PageResult<>();
        pageResult.setList(list);
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setTotal(total);
        pageResult.setPages(size > 0 ? (total + size - 1) / size : 0L);
        return pageResult;
    }

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
}
