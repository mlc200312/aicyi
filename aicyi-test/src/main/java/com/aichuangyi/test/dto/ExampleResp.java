package com.aichuangyi.test.dto;

import com.aichuangyi.base.core.DtoBean;
import com.aichuangyi.base.lang.BaseBean;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-05-27
 **/
@Getter
@Setter
@FieldNameConstants
public class ExampleResp extends BaseBean implements DtoBean {
    private String uuid;
    private Integer idx;
    private Integer status;
    private String amount;
    private String score;
    private String date;
    private String localDate;
    private String dateTime;
    private String timestamp;
    private String season;
    private Integer week;
    private List<String> idList;
    private String user;
    private StudentResp student;
    private String nothing;
}
