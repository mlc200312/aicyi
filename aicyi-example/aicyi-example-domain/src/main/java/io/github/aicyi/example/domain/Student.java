package io.github.aicyi.example.domain;

import io.github.aicyi.commons.util.DateTimeUtils;
import io.github.aicyi.example.domain.type.GradeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-05-21
 **/
@Getter
@Setter
@FieldNameConstants
public class Student extends User {
    private GradeType gradeType;
    private Double score;
    @JsonFormat(pattern = DateTimeUtils.ISO_DATE_TIME_PATTERN)
    private LocalDateTime registerTime;
    @JsonFormat(pattern = DateTimeUtils.DATE_TIME_PATTERN)
    private Date createTime;
    private Timestamp updateTime;
}
