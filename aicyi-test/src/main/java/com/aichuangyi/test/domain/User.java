package com.aichuangyi.test.domain;

import com.aichuangyi.commons.lang.BoBean;
import com.aichuangyi.commons.lang.BaseBean;
import com.aichuangyi.test.domain.type.GenderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-05-14
 **/
@Getter
@Setter
@FieldNameConstants
public class User extends BaseBean implements BoBean {
    private Long id;
    private Integer age;
    private String idCard;
    private String userName;
    private String mobile;
    private GenderType genderType;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate birthday;
}
