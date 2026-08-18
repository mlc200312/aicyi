package io.github.aicyi.commons.lang.model;

/**
 * @author Mr.Min
 * @description 分页参数对象
 * @date 15:50
 **/
public class PageParam extends BaseBean {

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 1;

    /**
     * 默认每页条数
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * 每页条数上限，防止深分页拖垮存储层
     */
    public static final int MAX_SIZE = 500;

    private Integer page;
    private Integer size;

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

    /**
     * 页码兑底：null 或非正数时返回默认页码（1）
     */
    public int getPageOrDefault() {
        return page == null || page <= 0 ? DEFAULT_PAGE : page;
    }

    /**
     * 每页条数兑底：null 或非正数时返回默认条数（10），超过上限时截断为 {@link #MAX_SIZE}
     */
    public int getSizeOrDefault() {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
