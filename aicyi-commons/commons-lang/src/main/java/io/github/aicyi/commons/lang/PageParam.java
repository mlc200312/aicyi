package io.github.aicyi.commons.lang;

import io.github.aicyi.commons.core.BoBean;

/**
 * @author Mr.Min
 * @description 分页参数对象
 * @date 15:50
 **/
public class PageParam extends BaseBean implements BoBean {
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
}
