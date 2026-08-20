package io.github.aicyi.midware.web.model;

import io.github.aicyi.commons.lang.model.BaseBean;

import javax.validation.constraints.Max;
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
     * 每页条数，上限 500，防止超大分页拖垮数据库；需更大值时由业务自行扩展子类覆盖
     */
    @Positive
    @Max(500)
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
