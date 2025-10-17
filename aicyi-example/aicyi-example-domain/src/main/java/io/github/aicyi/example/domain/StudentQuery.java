package io.github.aicyi.example.domain;

import io.github.aicyi.commons.lang.BaseBean;
import io.github.aicyi.commons.lang.DoBean;
import io.github.aicyi.example.domain.type.GradeType;
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
public class StudentQuery extends BaseBean implements DoBean {
    private GradeType gradeTypeEq;
    private LocalDate registerTimeStart;
    private LocalDate registerTimeEnd;
}
