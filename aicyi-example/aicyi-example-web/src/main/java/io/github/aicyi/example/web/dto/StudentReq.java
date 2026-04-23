package io.github.aicyi.example.web.dto;

import io.github.aicyi.midware.web.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:05
 **/
@Getter
@Setter
@ApiModel("学生对象请求参数")
public class StudentReq extends PageRequest {
    @ApiModelProperty("用户ID")
    private String userIdEq;
    @ApiModelProperty("班级")
    private String gradeTypeEq;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}$",
            message = "日期格式必须为 yyyy-MM-dd HH:mm:ss.SSS")
    @NotBlank
    @ApiModelProperty("注册开始时间")
    private String registerTimeStart;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}$",
            message = "日期格式必须为 yyyy-MM-dd HH:mm:ss.SSS")
    @NotBlank
    @ApiModelProperty("注册结束时间")
    private String registerTimeEnd;
}
