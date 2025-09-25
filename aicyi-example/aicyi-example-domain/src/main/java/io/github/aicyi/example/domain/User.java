package io.github.aicyi.example.domain;

import io.github.aicyi.commons.lang.BoBean;
import io.github.aicyi.commons.lang.UserInfo;
import io.github.aicyi.example.domain.type.GenderType;
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
public class User extends UserInfo implements BoBean {
    private Long id;
    private Integer age;
    private String idCard;
    private String mobile;
    private GenderType genderType;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate birthday;
}
