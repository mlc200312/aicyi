package io.github.aicyi.midware.web;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.lang.DtoBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Mr.Min
 * @description 分页请求对象
 * @date 15:12
 **/
@ApiModel("分页请求对象")
public class PageRequest extends BaseBean implements DtoBean {
    /**
     * 当前页码
     */
    @Positive
    @NotNull
    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer page;

    /**
     * 每页条数
     */
    @Positive
    @NotNull
    @ApiModelProperty(value = "每页条数", example = "10")
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
