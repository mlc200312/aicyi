package io.github.aicyi.midware.web.model;

import io.github.aicyi.commons.lang.model.BaseBean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Mr.Min
 * @description 分页请求对象
 * @date 15:12
 **/
public class PageRequest extends BaseBean {
    /**
     * 当前页码
     */
    @Positive
    @NotNull
    private Integer page;

    /**
     * 每页条数
     */
    @Positive
    @NotNull
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
