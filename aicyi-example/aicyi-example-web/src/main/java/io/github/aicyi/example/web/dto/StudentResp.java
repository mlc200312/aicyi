package io.github.aicyi.example.web.dto;

import io.github.aicyi.commons.lang.DtoBean;
import io.github.aicyi.commons.lang.BaseBean;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mr.Min
 * @description 学生类DTO
 * @date 2019-05-27
 **/
@Getter
@Setter
@ApiModel("登陆响应参数")
public class StudentResp extends BaseBean implements DtoBean {
    private String id;
    private Integer age;
    private String idCard;
    private String userName;
    private String mobile;
    private Integer genderType;
    private String birthday;
    private String gradeType;
    private String score0;
    private String registerTime;
    private String createTime;
    private String updateTime;
}
