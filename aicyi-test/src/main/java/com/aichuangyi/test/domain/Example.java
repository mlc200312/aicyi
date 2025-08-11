package com.aichuangyi.test.domain;

import com.aichuangyi.commons.core.BoBean;
import com.aichuangyi.commons.lang.BaseBean;
import com.aichuangyi.commons.lang.type.BooleanType;
import com.aichuangyi.test.domain.type.Season;
import com.aichuangyi.test.domain.type.Week;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-05-21
 **/
@Getter
@Setter
@FieldNameConstants
public class Example extends BaseBean implements BoBean {
    private Long id;
    private Integer idx;
    private BooleanType status;
    private BigDecimal amount;
    private Double score;
    private Date date;
    private LocalDate localDate;
    private LocalDateTime dateTime;
    private Timestamp timestamp;
    private Season season;
    private Week week;
    private List<Long> idList;
    private User user;
    private Student student;
    private String nothing;
}
