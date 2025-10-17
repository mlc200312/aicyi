package io.github.aicyi.example.domain;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.lang.DoBean;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:08
 **/
@Getter
@Setter
public class UserQuery extends BaseBean implements DoBean {
    private String mobileEq;
    private String idCardEq;
    private LocalDate birthdayStart;
    private LocalDate birthdayEnd;
}
