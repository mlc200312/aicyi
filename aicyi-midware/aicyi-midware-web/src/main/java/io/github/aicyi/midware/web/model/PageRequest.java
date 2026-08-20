package io.github.aicyi.midware.web.model;

import io.github.aicyi.commons.lang.model.BaseBean;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Mr.Min
 * @description 分页请求对象（Web 层输入，JSR-303 强校验）
 * <p>
 * 分工说明：Web 层接口入参使用本类（配合 @Valid）；服务层/通用层可使用
 * aicyi-commons-lang 的 {@code PageParam}（null/非法值兑底）
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
